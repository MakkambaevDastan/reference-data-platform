```
npm run dev
```

how to create project

npm create vite@latest admin -- --template vue-ts
cd admin
npm install

install TanStack modules
npm install @tanstack/vue-query @tanstack/vue-table @tanstack/vue-router @tanstack/vue-form

install tools and index
npm install -D @tanstack/router-plugin openapi-typescript
npm install vue-index axios

add to script `package.json`
```json
{
  "scripts": {
    "generate-api": "npx openapi-typescript ./schema.json -o ./src/types/schema.ts"
  }
}
```

npm run generate-api
