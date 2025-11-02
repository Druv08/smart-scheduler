/* ==========================
   SMART SCHEDULER - THEME SYSTEM
   Global Dark/Light Mode Toggle
========================== */

/**
 * Theme Management System
 * Handles theme switching and persistence across all pages
 */
class ThemeManager {
  constructor() {
    this.themeToggle = null;
    this.themeIcon = null;
    this.currentTheme = 'light';
    
    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', () => this.init());
    } else {
      this.init();
    }
  }

  /**
   * Initialize the theme system
   */
  init() {
    try {
      // Get saved theme from localStorage or default to 'light'
      this.currentTheme = localStorage.getItem('smart-scheduler-theme') || 'light';
      
      // Apply theme immediately to prevent flash
      this.applyTheme(this.currentTheme);
      
      // Set up toggle button
      this.setupToggleButton();
      
      // Update icon
      this.updateIcon();
      
      console.log('Ô£à Theme system initialized:', this.currentTheme);
    } catch (error) {
      console.error('ÔØî Theme system initialization failed:', error);
      // Fallback to light theme if there's an error
      this.applyTheme('light');
    }
  }

  /**
   * Set up the theme toggle button
   */
  setupToggleButton() {
    this.themeToggle = document.getElementById('themeToggle');
    this.themeIcon = document.getElementById('themeIcon');
    
    if (!this.themeToggle) {
      console.warn('ÔÜá´©Å Theme toggle button not found in DOM');
      return;
    }

    // Add click event listener
    this.themeToggle.addEventListener('click', (e) => {
      e.preventDefault();
      this.toggleTheme();
    });

    // Add keyboard support for accessibility
    this.themeToggle.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        this.toggleTheme();
      }
    });

    // Set accessibility attributes
    this.themeToggle.setAttribute('role', 'button');
    this.themeToggle.setAttribute('aria-label', `Switch to ${this.currentTheme === 'dark' ? 'light' : 'dark'} mode`);
    this.themeToggle.setAttribute('title', `Switch to ${this.currentTheme === 'dark' ? 'light' : 'dark'} mode`);
  }

  /**
   * Toggle between light and dark themes
   */
  toggleTheme() {
    const newTheme = this.currentTheme === 'dark' ? 'light' : 'dark';
    
    // Add animation class for smooth transition
    document.body.style.transition = 'all 0.3s ease';
    
    // Apply new theme
    this.applyTheme(newTheme);
    
    // Save to localStorage
    this.saveTheme(newTheme);
    
    // Update current theme
    this.currentTheme = newTheme;
    
    // Update icon and accessibility attributes
    this.updateIcon();
    this.updateAccessibility();
    
    // Remove transition after animation completes
    setTimeout(() => {
      document.body.style.transition = '';
    }, 300);
    
    console.log('­ƒöä Theme switched to:', newTheme);
  }

  /**
   * Apply theme to the document
   */
  applyTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    
    // Also add class for CSS compatibility
    document.documentElement.className = document.documentElement.className
      .replace(/theme-\w+/g, '') + ` theme-${theme}`;
  }

  /**
   * Save theme preference to localStorage
   */
  saveTheme(theme) {
    try {
      localStorage.setItem('smart-scheduler-theme', theme);
    } catch (error) {
      console.error('ÔØî Failed to save theme to localStorage:', error);
    }
  }

  /**
   * Update the theme toggle icon
   */
  updateIcon() {
    if (!this.themeIcon) return;
    
    const icons = {
      light: '­ƒîÖ', // Show moon when in light mode (to switch to dark)
      dark: 'ÔÿÇ´©Å'   // Show sun when in dark mode (to switch to light)
    };
    
    this.themeIcon.textContent = icons[this.currentTheme];
    
    // Add subtle animation
    this.themeIcon.style.transform = 'scale(0.8)';
    setTimeout(() => {
      this.themeIcon.style.transform = 'scale(1)';
    }, 150);
  }

  /**
   * Update accessibility attributes
   */
  updateAccessibility() {
    if (!this.themeToggle) return;
    
    const nextTheme = this.currentTheme === 'dark' ? 'light' : 'dark';
    this.themeToggle.setAttribute('aria-label', `Switch to ${nextTheme} mode`);
    this.themeToggle.setAttribute('title', `Switch to ${nextTheme} mode`);
  }

  /**
   * Get current theme
   */
  getCurrentTheme() {
    return this.currentTheme;
  }

  /**
   * Set theme programmatically
   */
  setTheme(theme) {
    if (!['light', 'dark'].includes(theme)) {
      console.error('ÔØî Invalid theme:', theme);
      return;
    }
    
    this.applyTheme(theme);
    this.saveTheme(theme);
    this.currentTheme = theme;
    this.updateIcon();
    this.updateAccessibility();
  }

  /**
   * Listen for system theme changes
   */
  initSystemThemeDetection() {
    // Check if user prefers dark mode
    if (window.matchMedia && !localStorage.getItem('smart-scheduler-theme')) {
      const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
      
      // Set initial theme based on system preference
      this.setTheme(mediaQuery.matches ? 'dark' : 'light');
      
      // Listen for changes
      mediaQuery.addEventListener('change', (e) => {
        // Only change if user hasn't manually set a preference
        if (!localStorage.getItem('smart-scheduler-theme')) {
          this.setTheme(e.matches ? 'dark' : 'light');
        }
      });
    }
  }
}

// Initialize theme manager
const themeManager = new ThemeManager();

// Make it globally available for debugging
window.ThemeManager = themeManager;

// Export for potential module usage
if (typeof module !== 'undefined' && module.exports) {
  module.exports = ThemeManager;
}
