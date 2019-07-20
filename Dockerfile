FROM nginx:1.13.3-alpine
COPY certs/server.crt /etc/ssl/
COPY certs/server.key /etc/ssl/
COPY nginx.conf /etc/nginx/

CMD ["nginx", "-g", "daemon off;"]