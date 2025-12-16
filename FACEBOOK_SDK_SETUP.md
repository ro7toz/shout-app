# Facebook SDK Setup Guide

## Overview
This document explains how the Facebook SDK is integrated into the Shout App for Instagram authentication and user data access.

## What Was Added

### 1. Frontend Integration (home.html)
```html
<script>
  window.fbAsyncInit = function() {
    FB.init({
      appId      : '[[${facebookAppId}]]',  // Injected from backend
      cookie     : true,
      xfbml      : true,
      version    : 'v19.0'
    });
    FB.AppEvents.logPageView();   
  };

  (function(d, s, id){
     var js, fjs = d.getElementsByTagName(s)[0];
     if (d.getElementById(id)) {return;}
     js = d.createElement(s); js.id = id;
     js.src = "https://connect.facebook.net/en_US/sdk.js";
     fjs.parentNode.insertBefore(js, fjs);
   }(document, 'script', 'facebook-jssdk'));
</script>
```

**Features:**
- ✅ Asynchronous SDK loading (doesn't block page rendering)
- ✅ Dynamic App ID injection from backend (via Thymeleaf)
- ✅ Cookie support for cross-domain tracking
- ✅ XFBML for Facebook plugins support
- ✅ Page view event logging

### 2. Backend Configuration (application.yml)
```yaml
facebook:
  app-id: ${FACEBOOK_APP_ID:}
  app-secret: ${FACEBOOK_APP_SECRET:}
  api-version: v19.0
```

**Environment Variables:**
- `FACEBOOK_APP_ID` - Your Facebook App ID (from Meta for Developers)
- `FACEBOOK_APP_SECRET` - Your Facebook App Secret (keep secure!)

### 3. Spring Boot Configuration (FacebookConfig.java)
```java
@Component
public class FacebookConfig {
    @Value("${facebook.app-id:}")
    private String appId;
    
    @Value("${facebook.app-secret:}")
    private String appSecret;
    
    @Value("${facebook.api-version:v19.0}")
    private String apiVersion;
    // getters...
}
```

### 4. Controller Injection (HomeController.java)
```java
@GetMapping("/")
public String home(Model model) {
    model.addAttribute("facebookAppId", facebookConfig.getAppId());
    return "home";
}
```

## Setup Instructions

### Step 1: Get Facebook App Credentials
1. Go to [Meta for Developers](https://developers.facebook.com)
2. Create a new app (if you haven't already)
3. In your app settings, find:
   - **App ID** (copy this)
   - **App Secret** (copy this and keep it safe!)
4. Go to "Settings" → "Basic" to view these values

### Step 2: Configure Instagram OAuth
1. In your Facebook app, go to "Products"
2. Click "Add Product" → Select "Instagram Graph API"
3. In Instagram Graph API settings:
   - Configure OAuth Redirect URI: `https://your-app.herokuapp.com/login/oauth2/code/instagram`
   - Request permissions:
     - `instagram_basic`
     - `user_public_profile`
     - `instagram_graph_user_profile`

### Step 3: Local Development
Create a `.env` file in your project root:
```bash
FACEBOOK_APP_ID=your_app_id_here
FACEBOOK_APP_SECRET=your_app_secret_here
INSTAGRAM_CLIENT_ID=your_instagram_client_id
INSTAGRAM_CLIENT_SECRET=your_instagram_client_secret
```

Load it in your IDE or use:
```bash
export FACEBOOK_APP_ID="your_app_id"
export FACEBOOK_APP_SECRET="your_app_secret"
```

### Step 4: Production Deployment (Heroku)
Set environment variables in Heroku dashboard:
```bash
heroku config:set FACEBOOK_APP_ID="your_app_id"
heroku config:set FACEBOOK_APP_SECRET="your_app_secret"
heroku config:set INSTAGRAM_CLIENT_ID="your_client_id"
heroku config:set INSTAGRAM_CLIENT_SECRET="your_client_secret"
```

## How It Works

### 1. User Visits Home Page
```
Browser → GET / → HomeController
         ↓
    Injects facebookAppId into Model
         ↓
    Renders home.html with FB App ID
         ↓
Facebook SDK initializes with App ID
```

### 2. Facebook SDK Loads Asynchronously
- SDK loads from CDN: `https://connect.facebook.net/en_US/sdk.js`
- Doesn't block page rendering
- `window.fbAsyncInit` executes when SDK is ready
- FB.init() initializes with your App ID
- FB.AppEvents.logPageView() logs the visit

### 3. User Authentication Flow
```
User clicks "Login with Instagram"
    ↓
Spring OAuth2 redirects to Instagram
    ↓
User approves permissions
    ↓
Instagram redirects back to: /login/oauth2/code/instagram
    ↓
Spring OAuth2 exchanges code for access token
    ↓
App creates user account & session
    ↓
User redirected to dashboard
```

## Security Best Practices

✅ **DO:**
- Store App Secret securely (never in code)
- Use environment variables for all credentials
- Use HTTPS in production
- Validate all tokens server-side
- Use CSRF protection (Spring Security does this)

❌ **DON'T:**
- Hardcode App ID or App Secret in code
- Expose App Secret in frontend code
- Use non-HTTPS URLs in production
- Trust client-side token validation alone

## Testing

### Verify SDK is Loading
1. Open browser DevTools (F12)
2. Go to Console tab
3. Type: `typeof FB` 
4. Should return: `"object"`
5. Type: `FB.getAppId()`
6. Should return: your App ID

### Verify App ID Injection
1. View page source (Ctrl+U or Cmd+U)
2. Look for: `appId      : '1229110702414697'` (your App ID)
3. Should NOT be empty

## Troubleshooting

### "App ID is empty" Error
**Solution:**
- Check environment variable is set: `echo $FACEBOOK_APP_ID`
- Verify it's in application.yml under `facebook.app-id`
- Restart Spring Boot server
- Check HomeController is injecting it to model

### "SDK Failed to Load" Error
**Solution:**
- Check browser console for CORS errors
- Verify CDN URL: `https://connect.facebook.net/en_US/sdk.js` is accessible
- Check internet connection
- Try clearing cache (Ctrl+Shift+Delete)

### "Authorization Failed" Error
**Solution:**
- Verify App Secret is correct in Heroku config
- Check OAuth redirect URI matches exactly: `https://your-app.herokuapp.com/login/oauth2/code/instagram`
- Verify Instagram permissions are requested
- Check Facebook App is not in development mode

## Files Modified/Created

```
✅ home.html - Added Facebook SDK script
✅ application.yml - Added facebook config section
✅ FacebookConfig.java - New Spring configuration class
✅ HomeController.java - New controller for home page
✅ FACEBOOK_SDK_SETUP.md - This file
```

## Next Steps

1. **Get your credentials** from Meta for Developers
2. **Set environment variables** (local and production)
3. **Test locally** by running Spring Boot and checking browser console
4. **Deploy to Heroku** with credentials set
5. **Test authentication** flow end-to-end

## References

- [Meta for Developers](https://developers.facebook.com)
- [Facebook SDK Documentation](https://developers.facebook.com/docs/javascript)
- [Instagram Graph API Docs](https://developers.facebook.com/docs/instagram-api)
- [Spring OAuth2 Documentation](https://spring.io/projects/spring-security-oauth2-client)
