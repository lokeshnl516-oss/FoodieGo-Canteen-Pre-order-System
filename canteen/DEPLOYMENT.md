# FoodieGo Frontend - Deployment Guide

## Local Development Setup

### Option 1: Python Built-in Server (Recommended)
```bash
cd foodiego-frontend
python -m http.server 8000
# Access at http://localhost:8000
```

### Option 2: Node.js http-server
```bash
npm install -g http-server
cd foodiego-frontend
http-server
# Access at http://localhost:8080
```

### Option 3: VS Code Live Server
1. Install "Live Server" extension
2. Right-click index.html
3. Select "Open with Live Server"

## Production Deployment

### Option 1: Deploy with Backend (Same Server)
Place frontend files in Spring Boot `src/main/resources/static/`:
```
src/main/resources/static/
├── index.html
├── styles.css
└── js/
    ├── api.js
    ├── auth.js
    ├── user.js
    ├── admin.js
    └── app.js
```

Then access at: `http://yourdomain.com/`

### Option 2: Deploy on Separate Server
- **Vercel**: Push to GitHub, connect to Vercel (FREE)
- **Netlify**: Drag & drop or Git integration
- **Firebase Hosting**: Firebase CLI deployment
- **AWS S3 + CloudFront**: Static hosting
- **Heroku Static File Serving**: Using buildpack

### Option 3: Docker Deployment
Create `Dockerfile`:
```dockerfile
FROM nginx:alpine
COPY . /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

Create `nginx.conf`:
```nginx
server {
    listen 80;
    root /usr/share/nginx/html;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

Build & run:
```bash
docker build -t foodiego-frontend .
docker run -p 80:80 foodiego-frontend
```

## Configuration

### Update API Base URL
Modify `js/api.js`:

**Development:**
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

**Production:**
```javascript
const API_BASE_URL = 'https://yourdomain.com/api';
```

### CORS Configuration
Ensure backend allows frontend origin in `SecurityConfig.java`:
```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000",
    "https://yourdomain.com"
));
```

## Performance Optimization

### Minification
Minify CSS and JavaScript:
```bash
npm install -g minify
minify styles.css > styles.min.css
minify js/api.js > js/api.min.js
```

Update HTML references:
```html
<link rel="stylesheet" href="styles.min.css">
<script src="js/api.min.js"></script>
```

### Caching Headers
```bash
# In your web server config
Cache-Control: max-age=31536000 (for styles.css, js files)
Cache-Control: no-cache (for index.html)
```

### CDN Integration
Host static files on CDN (Cloudflare, jsDelivr, etc.):
```html
<script src="https://cdn.jsdelivr.net/npm/foodiego@1.0.0/js/api.js"></script>
```

## Troubleshooting

### CORS Errors
- Ensure backend CORS is properly configured
- Update API_BASE_URL to match backend domain
- Check browser console for exact error

### Blank Page
- Check browser console for JavaScript errors
- Verify all JS files are loading (Network tab)
- Ensure backend is running

### Cart Not Working
- Check if LocalStorage is enabled
- Try clearing browser cache
- Check browser console for errors

### JWT Token Issues
- Token expires after 24 hours (configurable in backend)
- Logout and login again for fresh token
- Check token in browser DevTools > Application > LocalStorage

## Security Checklist

- [ ] Use HTTPS in production
- [ ] Implement rate limiting on backend
- [ ] Validate all inputs on backend
- [ ] Use HttpOnly cookies for production (not LocalStorage)
- [ ] Implement CSRF protection
- [ ] Set proper HTTP headers (CSP, X-Frame-Options)
- [ ] Sanitize user inputs
- [ ] Keep dependencies updated

## Monitoring

### Google Analytics
Add to `index.html`:
```html
<script async src="https://www.googletagmanager.com/gtag/js?id=GA_ID"></script>
<script>
  window.dataLayer = window.dataLayer || [];
  function gtag(){dataLayer.push(arguments);}
  gtag('js', new Date());
  gtag('config', 'GA_ID');
</script>
```

### Error Tracking (Sentry)
```html
<script src="https://browser.sentry-cdn.com/x.x.x/bundle.min.js"></script>
<script>
  Sentry.init({ dsn: 'YOUR_DSN' });
</script>
```

## Rollback Procedure

1. Keep backup of previous version
2. Use Git for version control
3. Deploy to staging first
4. Test thoroughly before production
5. Have rollback plan ready

## Testing Checklist

- [ ] All pages load correctly
- [ ] Login/Register works
- [ ] Menu displays properly
- [ ] Cart functionality works
- [ ] Orders can be placed
- [ ] Admin features work
- [ ] Responsive design looks good
- [ ] No console errors
- [ ] API calls succeed
- [ ] Logout clears data
