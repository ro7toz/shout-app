# ShoutX Frontend - Complete HTML/CSS/JS Specifications

## Color System

```css
:root {
  --primary: #2D7A8E;      /* Teal - Main brand color */
  --primary-light: #5BA3B8;
  --primary-dark: #1F5A69;
  --secondary: #F5F5F5;    /* Light gray */
  --text: #333333;
  --text-light: #666666;
  --white: #FFFFFF;
  --error: #F44336;
  --success: #4CAF50;
  --warning: #FF9800;
}
```

## Typography

```css
body {
  font-family: 'Segoe UI', Tahoma, sans-serif;
  font-size: 14px;
  line-height: 1.6;
  color: var(--text);
}

h1 { font-size: 32px; font-weight: 600; }
h2 { font-size: 24px; font-weight: 600; }
h3 { font-size: 18px; font-weight: 600; }
p { font-size: 14px; }
```

## Pages Implementation Checklist

### Page: Homepage (No Login)
- [ ] Hero section with CTA
- [ ] Features grid (3 items)
- [ ] FAQ accordion
- [ ] Footer with all links
- [ ] Mobile responsive
- [ ] SEO meta tags

### Page: Homepage (After Login) - Dashboard
- [ ] Tab switcher (Send ShoutOuts / Requests)
- [ ] Filter & sort button
- [ ] User cards grid
- [ ] Request cards list
- [ ] Notification badge
- [ ] 24-hour countdown timer
- [ ] Mobile responsive

### Page: User Profile (Others)
- [ ] Profile header (DP, name, followers)
- [ ] Verification badge
- [ ] Rating display
- [ ] Media grid (1-3 photos)
- [ ] Repost buttons
- [ ] Mobile responsive

### Page: User Profile (Self)
- [ ] All features from profile + edit capability
- [ ] Add media button
- [ ] Delete media button (with constraint: min 1 photo)
- [ ] Flag button
- [ ] Mobile responsive

### Page: Notifications
- [ ] List of notifications
- [ ] Mark as read functionality
- [ ] Delete notification
- [ ] Empty state
- [ ] Mobile responsive

### Page: Payments
- [ ] Plan comparison cards
- [ ] Payment method selection (UPI/PayPal/Paytm)
- [ ] Form submission
- [ ] Success/error messages
- [ ] Mobile responsive

### Page: Terms & Conditions
- [ ] Professional content
- [ ] Proper headings
- [ ] SEO tags
- [ ] Mobile responsive

### Page: Privacy Policy
- [ ] Professional content
- [ ] Proper headings
- [ ] SEO tags
- [ ] Mobile responsive

### Page: Refund Policy
- [ ] Professional content
- [ ] Proper headings
- [ ] SEO tags
- [ ] Mobile responsive

## Modal Implementation Checklist

### Modal: Login/Signup
- [ ] Email/password input
- [ ] Instagram OAuth button
- [ ] Form validation
- [ ] Error handling
- [ ] Tab switching (Login/Signup)
- [ ] Mobile responsive

### Modal: Plans & Pricing
- [ ] Plan comparison
- [ ] Price display
- [ ] Features list
- [ ] CTA button
- [ ] Mobile responsive

### Modal: Exchange Details
- [ ] Completion status
- [ ] Time remaining
- [ ] Action buttons
- [ ] Rating section
- [ ] Mobile responsive

## JavaScript Functionality Required

1. **Modal Management**
   - Open/close modals
   - Tab switching within modals
   - Click outside to close

2. **Tab Switching**
   - Send ShoutOuts tab
   - Requests tab
   - Active state management

3. **Filter & Sort**
   - Genre filter
   - Follower range filter
   - Repost type filter
   - Sort options

4. **Timer**
   - 24-hour countdown
   - Update every second
   - Alert on expiry

5. **Form Validation**
   - Email validation
   - Password strength
   - File upload validation

6. **Notifications**
   - Show notification dot
   - Display notification list
   - Mark as read

7. **Analytics**
   - Draw charts/graphs
   - Update data periodically

## CSS Modules Required

1. **variables.css** - CSS custom properties
2. **layout.css** - Grid, flexbox, containers
3. **typography.css** - Fonts, headings, text styles
4. **components.css** - Buttons, cards, forms, modals
5. **responsive.css** - Media queries for mobile/tablet/desktop

## Asset Requirements

- Logo (SVG or PNG)
- Icons (user, search, filter, repost, delete, close, etc.)
- Placeholder images

## Performance Targets

- Page load: < 2 seconds
- First paint: < 1 second
- API response: < 500ms
- Lighthouse score: > 90

## Accessibility Requirements

- WCAG 2.1 AA compliance
- Keyboard navigation
- Color contrast 4.5:1 for text
- ARIA labels
- Alt text for images

## SEO Requirements

- Meta title (55 chars)
- Meta description (155 chars)
- H1 per page
- Image alt text
- Internal linking
- Schema markup
- Mobile friendly

## Cross-browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Mobile Support

- iPhone 6+
- Android 8+
- Responsive breakpoints: 320px, 768px, 1024px, 1440px
