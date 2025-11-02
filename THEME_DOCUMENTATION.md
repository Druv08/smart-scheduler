# Smart Scheduler - Universal Theme System

## ­ƒÄ¿ Overview
The Smart Scheduler now features a comprehensive dark/light mode toggle system that works across all pages and components.

## Ô£¿ Features

### ­ƒîÖ Theme Toggle
- **Location**: Top-right corner of the navigation bar (next to notifications and profile)
- **Icons**: ÔÿÇ´©Å (light mode) / ­ƒîÖ (dark mode)
- **Accessibility**: Full keyboard support with Tab navigation and Enter/Space activation

### ­ƒÆ¥ Persistence
- **Storage**: User preference saved in `localStorage` as `smart-scheduler-theme`
- **Cross-Page**: Theme persists when navigating between different pages
- **Session**: Theme choice maintained across browser sessions

### ­ƒöä Auto-Detection
- **System Preference**: Automatically detects OS-level dark/light mode preference on first visit
- **Dynamic Updates**: Responds to system theme changes in real-time
- **Fallback**: Defaults to light mode if no preference is detected

### ­ƒÄ» Component Coverage
All UI components are fully themed:
- **Navigation**: Header, navbar, links
- **Cards**: Course cards, faculty cards, room cards
- **Tables**: Headers, rows, borders, hover states
- **Forms**: Input fields, dropdowns, buttons
- **Modals**: Background, content, headers
- **Notifications**: Success, error, info messages
- **Pagination**: Controls and states

## ­ƒøá Technical Implementation

### File Structure
```
css/
  Ôö£ÔöÇÔöÇ theme.css              # Theme variables and component styles
  ÔööÔöÇÔöÇ modern-exact-design.css # Base design system

js/
  ÔööÔöÇÔöÇ theme.js               # Theme management logic
```

### CSS Variables
The theme system uses CSS custom properties for consistent theming:

```css
:root {
  --theme-bg-primary: #ffffff;
  --theme-text-primary: #111827;
  --theme-surface-primary: #ffffff;
  /* ... more variables */
}

[data-theme="dark"] {
  --theme-bg-primary: #0f172a;
  --theme-text-primary: #f1f5f9;
  --theme-surface-primary: #1e293b;
  /* ... dark variants */
}
```

### JavaScript API
```javascript
// Access the theme manager
const themeManager = window.themeManager;

// Get current theme
const currentTheme = themeManager.getCurrentTheme(); // 'light' or 'dark'

// Check if dark mode is active
const isDark = themeManager.isDarkMode(); // boolean

// Manually set theme (not recommended - use toggle button)
themeManager.setTheme('dark');
```

## ­ƒÜÇ Usage

### For Users
1. **Toggle Theme**: Click the ­ƒîÖ/ÔÿÇ´©Å button in the top-right corner
2. **Keyboard Access**: Tab to the theme button and press Enter or Space
3. **Automatic**: Your preference is remembered for future visits

### For Developers
1. **Include Files**: Add theme.css and theme.js to all HTML pages
2. **Use Variables**: Replace hardcoded colors with CSS theme variables
3. **Test**: Use the theme-demo.html page to test new components

## ­ƒô▒ Browser Support
- **Modern Browsers**: Chrome 88+, Firefox 85+, Safari 14+, Edge 88+
- **Features**: CSS custom properties, localStorage, matchMedia API
- **Fallback**: Graceful degradation to light theme on unsupported browsers

## ­ƒöº Customization

### Adding New Theme Colors
Edit `css/theme.css` and add new variables:
```css
:root {
  --theme-new-color: #your-light-color;
}

[data-theme="dark"] {
  --theme-new-color: #your-dark-color;
}
```

### Applying Theme to New Components
Use existing theme variables in your CSS:
```css
.my-component {
  background: var(--theme-surface-primary);
  color: var(--theme-text-primary);
  border: 1px solid var(--theme-border-primary);
}
```

## ­ƒôï Implementation Checklist

### Ô£à Completed
- [x] Theme CSS variables for light and dark modes
- [x] JavaScript theme management system
- [x] Auto-detection of system preference
- [x] LocalStorage persistence
- [x] Theme toggle button integration
- [x] All major pages updated (courses.html, users.html, dashboard.html, etc.)
- [x] Form elements themed (inputs, dropdowns, buttons)
- [x] Table components themed
- [x] Modal dialogs themed
- [x] Smooth transitions between themes
- [x] Accessibility support (keyboard navigation)
- [x] Mobile meta theme-color support
- [x] Demo page for testing

### ­ƒÄ» Pages Covered
- [x] index.html (Homepage)
- [x] dashboard.html (Main Dashboard)
- [x] courses.html (Course Management)
- [x] users.html (Faculty Management)
- [x] rooms.html (Room Management)
- [x] timetable.html (Timetable View)
- [x] theme-demo.html (Feature Demo)

## ­ƒÉø Troubleshooting

### Theme Not Persisting
- Check browser localStorage permissions
- Verify theme.js is loaded before other scripts
- Clear browser cache if needed

### Colors Not Updating
- Ensure CSS theme variables are used instead of hardcoded colors
- Check for CSS specificity conflicts
- Verify theme.css is loaded after base styles

### Toggle Button Missing
- Confirm theme.js is loaded
- Check that the header has a `.actions` container
- Verify JavaScript console for errors

## ­ƒôê Performance
- **CSS Variables**: Efficient theme switching without recalculating styles
- **Smooth Transitions**: 0.3s CSS transitions for visual feedback
- **Lazy Loading**: Theme applied immediately on page load
- **Minimal Impact**: < 5KB additional CSS/JS for complete theme system
