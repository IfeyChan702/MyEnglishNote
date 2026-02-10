server {
listen 443 ssl; # managed by Certbot
listen [::]:443 ssl; # managed by Certbot
server_name amazone.bbypay.net; # managed by Certbot

    root /opt/ifey/amazonepay/web;
    index index.html;

    location / {
        # 前端 SPA 应用需要 fallback 到 index.html
        try_files $uri $uri/ /index.html;
    }

    location /prod-api/ {
    proxy_pass http://127.0.0.1:9501/;   # 你的后端服务地址
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    
    ssl_certificate /etc/letsencrypt/live/amazone.bbypay.net/fullchain.pem; # managed by Certbot
    ssl_certificate_key /etc/letsencrypt/live/amazone.bbypay.net/privkey.pem; # managed by Certbot
    include /etc/letsencrypt/options-ssl-nginx.conf; # managed by Certbot
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem; # managed by Certbot
}

server {
listen 80;
listen [::]:80;
server_name amazone.bbypay.net;

    # 强制跳转到 https
    return 301 https://$host$request_uri;
}
