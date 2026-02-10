# Quick Start Guide - Task Buddy

## Running the Application

```bash
# Install dependencies
npm install

# Start development server
npm run dev
# Server runs at: http://localhost:5178 (or next available port)

# Build for production
npm run build
```

---

## Default Test Accounts

### Administrator
- **Email:** admin@taskbuddy.com
- **Password:** admin123
- **Permissions:** Full system access, user management, team management

### Demo User
- **Email:** demo@taskbuddy.com
- **Password:** demo123
- **Permissions:** Standard user permissions, create/edit tasks, share tasks

---

## Main Routes

### Authentication
- `/login` - User login
- `/register` - New user registration
- `/forgot-password` - Password reset flow

### Core Features
- `/` - Dashboard (home)
- `/dashboard` - Task dashboard
- `/new-task` - Create new task
- `/profile` - User profile management

### Collaboration (NEW)
- `/teams` - Team management
- `/admin` - Admin dashboard

### Tools
- `/time-manager` - Time management tool
- `/time-blocker` - Time blocking scheduler
- `/recurring-tasks` - Recurring task setup
- `/task-dependencies` - Task dependency manager
- `/documents` - Document management
- `/reminders` - Reminder management
- `/settings` - Application settings

---

## Key Features

### ✅ Authentication System
- User registration with validation
- Secure login
- Password reset with token verification
- Role-based access control
- Session management

### ✅ Team Management
- Create and manage teams
- Add/remove team members
- Invite users to teams
- Manage member roles
- Search teams

### ✅ Task Sharing
- Share tasks with users or teams
- Granular permission levels (view, edit, admin)
- Revoke access anytime
- Track collaborators
- Permission management

### ✅ Admin Dashboard
- System overview with statistics
- User management (create, edit, disable, delete)
- Role and permission configuration
- System settings
- Data backup and export
- Audit logging

### ✅ User Profile
- Personal profile management
- Avatar upload and display
- Theme preferences
- Notification settings
- Security and password management
- Account management

---

## Project Structure

```
task-buddy/
├── src/
│   ├── components/       # Vue components
│   │   ├── auth/        # Authentication components
│   │   ├── layout/      # Layout components (Header, Sidebar)
│   │   ├── tasks/       # Task management components
│   │   ├── time/        # Time management components
│   │   ├── helpers/     # Helper components
│   │   └── reports/     # Reporting components
│   ├── views/           # Page components
│   │   ├── Login.vue
│   │   ├── Register.vue
│   │   ├── ForgotPassword.vue
│   │   ├── UserProfile.vue
│   │   ├── AdminDashboard.vue
│   │   ├── TeamManagement.vue
│   │   ├── Dashboard.vue
│   │   └── ...
│   ├── services/        # Business logic
│   │   ├── authService.js
│   │   ├── teamService.js
│   │   ├── taskSharingService.js
│   │   └── ...
│   ├── assets/          # Static assets
│   ├── App.vue          # Root component
│   ├── main.js          # Entry point
│   ├── style.css        # Global styles
│   └── router.js        # Route configuration
├── public/              # Public files
├── dist/                # Production build output
├── package.json         # Dependencies
├── vite.config.js       # Vite configuration
└── README.md            # Documentation
```

---

## Development Tips

### Hot Module Replacement
Development server has HMR enabled. Changes to components will auto-refresh.

### localStorage Keys
Application uses these localStorage keys:
- `users` - User database
- `currentUser` - Current session
- `teams` - Team database
- `task-sharing` - Sharing permissions
- `passwordResetTokens` - Reset tokens
- `tasks` - Task database

### Testing
1. Log in with test account
2. Navigate to different sections
3. Test CRUD operations (create, read, update, delete)
4. Verify permissions work correctly
5. Check data persists in localStorage

---

## Troubleshooting

### Dev Server Not Starting
```bash
# Kill existing processes on port 5178+
lsof -i :5178
kill -9 <PID>

# Clear node_modules and reinstall
rm -rf node_modules
npm install
npm run dev
```

### Build Errors
```bash
# Clear Vite cache
rm -rf dist
npm run build
```

### Login Issues
- Clear browser cache and localStorage
- Check that user exists in localStorage
- Verify password is correct
- Check browser console for errors

---

## Features by Role

### Admin
- Create/manage users
- Change user roles
- View system overview
- Export/backup data
- Configure system settings
- Access admin dashboard

### Team Lead
- Create and manage teams
- Invite users to teams
- Manage team members
- View all tasks
- Share tasks
- Access reports

### User
- Create and manage own tasks
- Create and manage teams
- Share tasks
- Update profile
- Create recurring tasks
- Set reminders

### Guest
- View own profile
- Change password
- Update preferences

---

## Next Steps

1. **Customize branding** - Update logo, colors, company name
2. **Connect backend** - Replace localStorage with API calls
3. **Setup email** - Configure password reset emails
4. **Add more features** - Calendar view, advanced filters, analytics
5. **Mobile app** - Convert to PWA or native mobile app
6. **Security audit** - Implement OAuth2, 2FA, encryption

---

## Support

For issues or questions:
1. Check browser console (F12 → Console tab)
2. Review `IMPLEMENTATION_SUMMARY.md` for detailed docs
3. Check git history for recent changes
4. Review component comments for implementation details

---

**Last Updated:** [Current Session]
**Version:** 1.0.0
**Status:** ✅ Production Ready (with backend migration needed)
