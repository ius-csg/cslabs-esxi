FROM nginx:1.13.3-alpine
COPY server.crt /etc/ssl/
COPY server.key /etc/ssl/
COPY nginx.conf /etc/nginx/

CMD ["nginx", "-g", "daemon off;"]