# ---- Build stage ----
FROM node:20-alpine AS build

WORKDIR /app

# Install dependencies first (better layer caching — only reinstalls when
# package*.json actually changes, not on every source edit)
COPY package*.json ./
RUN npm ci

# Copy the rest of the source and build
COPY . .

# Vite bakes VITE_* env vars into the bundle at build time, so it must be
# passed as a build arg, not a runtime env var.
ARG VITE_GATEWAY_BASE_URL=http://localhost:8961
ENV VITE_GATEWAY_BASE_URL=$VITE_GATEWAY_BASE_URL

RUN npm run build

# ---- Serve stage ----
FROM nginx:1.27-alpine

# Custom config: SPA fallback so React Router routes (e.g. /dashboard,
# /transport) work on a hard refresh instead of 404ing at the Nginx level.
COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]