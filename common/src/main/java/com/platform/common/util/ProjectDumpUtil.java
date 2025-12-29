package com.platform.common.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ProjectDumpUtil {

    private final Path rootPath;
    private final String targetPackage;
    private final Path outputPath;
    private final long maxFileSize;
    private final Set<String> excludedDirs;
    private final Set<String> excludedFilenames;
    private final List<PathMatcher> excludedFilePatterns;
    private final List<Pattern> excludedContentPatterns;
    private final Map<String, String> extensions;

    private ProjectDumpUtil(Builder builder) {
        rootPath = builder.rootPath;
        targetPackage = builder.targetPackage;
        outputPath = builder.outputPath;
        maxFileSize = builder.maxFileSize;
        excludedDirs = builder.excludedDirs;
        excludedFilenames = builder.excludedFilenames;
        extensions = builder.extensions;

        excludedFilePatterns = builder.excludedPatterns.stream()
                .map(pattern -> FileSystems.getDefault().getPathMatcher("glob:" + pattern))
                .toList();

        excludedContentPatterns = builder.excludedContent.stream()
                .map(Pattern::compile)
                .toList();
    }

    public static void main(String[] args) {
        try {
            ProjectDumpUtil.builder()
                    .root(Paths.get("."))
                    .targetPackage("com.platform.common")
                    .output(Paths.get("project.md"))
                    .addExcludedDir("test")
                    .addExcludedFilename("Config.java")
                    .addExcludedPattern("*Test.java")
                    .addExcludedPattern("*.log")
                    .addExcludedContent("^package .*")
                    .addExcludedContent("^import .*")
                    .addExcludedContent(".*LOGGER.*")
                    .addExcludedContent(".*// TODO.*")
                    .addExcludedContent("^\\s*$")
                    .build()
                    .dump();
        } catch (IOException e) {
            System.err.println("❌ Error creating dump: " + e.getMessage());
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public int dump() throws IOException {
        Path projectRoot = resolveProjectRoot(rootPath);

        String targetPathStr = targetPackage.replace(".", File.separator);
        List<Path> foundFiles = new ArrayList<>();

        System.out.println("🔍 Project Root: " + projectRoot);
        System.out.println("🎯 Target Package: " + targetPackage);

        Files.walkFileTree(projectRoot, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                String dirName = dir.getFileName().toString();
                if (excludedDirs.contains(dirName) || dirName.startsWith(".")) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (attrs.size() > maxFileSize) return FileVisitResult.CONTINUE;
                if (shouldExcludeFile(file)) return FileVisitResult.CONTINUE;

                String absPath = file.toAbsolutePath().toString();
                if (absPath.contains(targetPathStr)) {
                    String ext = getExtension(file);
                    if (extensions.containsKey(ext)) {
                        foundFiles.add(file);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });

        Collections.sort(foundFiles);

        if (foundFiles.isEmpty()) {
            System.out.println("⚠️ Files not found");
            return 0;
        }

        writeMarkdown(projectRoot, foundFiles);

        System.out.println("✅ Dump saved to: " + outputPath.toAbsolutePath());
        System.out.println("📊 Total files: " + foundFiles.size());
        return foundFiles.size();
    }

    private boolean shouldExcludeFile(Path file) {
        String fileName = file.getFileName().toString();

        if (excludedFilenames.contains(fileName)) return true;

        for (PathMatcher matcher : excludedFilePatterns) {
            if (matcher.matches(file.getFileName())) return true;
        }
        return false;
    }

    private boolean shouldExcludeLine(String line) {
        if (excludedContentPatterns.isEmpty()) return false;

        for (Pattern pattern : excludedContentPatterns) {
            if (pattern.matcher(line).find()) {
                return true;
            }
        }
        return false;
    }

    private void writeMarkdown(Path projectRoot, List<Path> foundFiles) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            // Header
            writer.write("# Project Dump\n\n");
            writer.write("- **Package:** `" + targetPackage + "`\n");
            writer.write("- **Date:** " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("- **Root:** `" + projectRoot.toAbsolutePath() + "`\n");
            writer.write("- **Total Files:** " + foundFiles.size() + "\n\n");
            writer.write("---\n\n");

            // Table of Contents
            writer.write("## 📑 Table of Contents\n\n");
            for (Path file : foundFiles) {
                String relativePath = getRelativePath(projectRoot, file);
                String anchor = relativePath.toLowerCase().replaceAll("[^a-z0-9/._-]", "").replace("/", "").replace(".", "");
                writer.write("- [" + relativePath + "](#" + anchor + ")\n");
            }
            writer.write("\n---\n\n");

            // File Content
            for (Path file : foundFiles) {
                writeFileContent(writer, file, projectRoot);
            }
        }
    }

    private void writeFileContent(BufferedWriter writer, Path file, Path root) throws IOException {
        String relativePath = getRelativePath(root, file);
        String ext = getExtension(file);
        String lang = extensions.getOrDefault(ext, "text");

        writer.write("### 📄 `" + relativePath + "`\n\n");
        writer.write("```" + lang + "\n");

        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            lines.filter(line -> !shouldExcludeLine(line))
                    .forEach(line -> {
                        try {
                            writer.write(line);
                            writer.newLine();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        } catch (UncheckedIOException | IOException e) {
            writer.write("// Error reading file: " + e.getMessage() + "\n");
        }

        writer.write("```\n\n---\n\n");
    }

    private String getRelativePath(Path root, Path file) {
        return root.relativize(file).toString().replace("\\", "/");
    }

    private static String getExtension(Path file) {
        String name = file.getFileName().toString();
        int idx = name.lastIndexOf('.');
        return idx == -1 ? "" : name.substring(idx);
    }

    private static Path resolveProjectRoot(Path start) {
        Path current = start.toAbsolutePath().normalize();
        while (current != null) {
            if (Files.exists(current.resolve("pom.xml")) ||
                    Files.exists(current.resolve("build.gradle")) ||
                    Files.exists(current.resolve(".git"))) {
                return current;
            }
            current = current.getParent();
        }
        return start;
    }

    public static class Builder {
        private Path rootPath = Paths.get(".");
        private String targetPackage = "";
        private Path outputPath = Paths.get("project.md");
        private long maxFileSize = 200 * 1024; // 200 KB
        private final Set<String> excludedDirs = new HashSet<>(Set.of(
                "target", ".git", ".idea", ".mvn", "node_modules",
                "build", "dist", ".gradle", "out", ".settings"
        ));
        private final Set<String> excludedFilenames = new HashSet<>();
        private final Set<String> excludedPatterns = new HashSet<>();
        private final Set<String> excludedContent = new HashSet<>();

        private final Map<String, String> extensions = new HashMap<>(Map.ofEntries(
                Map.entry(".java", "java"),
                Map.entry(".kt", "kotlin"),
                Map.entry(".xml", "xml"),
                Map.entry(".yaml", "yaml"),
                Map.entry(".yml", "yaml"),
                Map.entry(".json", "json"),
                Map.entry(".sql", "sql"),
                Map.entry(".properties", "properties"),
                Map.entry(".js", "javascript"),
                Map.entry(".ts", "typescript"),
                Map.entry(".html", "html"),
                Map.entry(".css", "css"),
                Map.entry(".md", "markdown"),
                Map.entry(".gitignore", "text")
        ));

        public Builder root(Path rootPath) {
            this.rootPath = rootPath;
            return this;
        }

        public Builder targetPackage(String targetPackage) {
            this.targetPackage = targetPackage;
            return this;
        }

        public Builder output(Path outputPath) {
            this.outputPath = outputPath;
            return this;
        }

        public Builder maxFileSize(long bytes) {
            maxFileSize = bytes;
            return this;
        }

        public Builder addExcludedDir(String dir) {
            excludedDirs.add(dir);
            return this;
        }

        public Builder addExcludedFilename(String filename) {
            excludedFilenames.add(filename);
            return this;
        }

        public Builder addExcludedPattern(String pattern) {
            excludedPatterns.add(pattern);
            return this;
        }

        public Builder addExcludedContent(String regex) {
            excludedContent.add(regex);
            return this;
        }

        public Builder addExtension(String ext, String lang) {
            extensions.put(ext, lang);
            return this;
        }

        public ProjectDumpUtil build() {
            if (targetPackage == null || targetPackage.isEmpty()) {
                throw new IllegalArgumentException("Target package must be set");
            }
            return new ProjectDumpUtil(this);
        }
    }
}