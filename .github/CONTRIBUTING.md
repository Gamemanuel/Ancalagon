# Contributing to AluminumDecoded

> [!NOTE]
> Welcome to Team 10523 - The Dragons! This guide will help you contribute to our robot codebase effectively.

Thank you for your interest in contributing to AluminumDecoded! This document provides guidelines for contributing to our FTC robot code. Whether you're fixing bugs, adding new features, or improving documentation, we appreciate your contributions!

---

## Table of Contents

- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Pull Request Guidelines](#pull-request-guidelines)
- [Reporting Issues](#reporting-issues)
- [Release Process](#release-process)
- [Code Standards](#code-standards)
- [Resources](#resources)

---

## Getting Started

### Prerequisites

Before contributing, make sure you have:

- [ ] **Android Studio** installed and configured
- [ ] **Git** installed on your machine
- [ ] Access to this repository
- [ ] Basic understanding of Java programming
- [ ] Familiarity with FTC SDK concepts

<details>
<summary>New to Git? Click here for resources</summary>

If you're new to Git and version control, check out these resources:

- [Git Book](https://git-scm.com/book/en/v2) - Comprehensive Git guide
- [Interactive Git Tutorial](https://try.github.io) - Learn by doing
- [GitHub Flow Guide](https://guides.github.com/introduction/flow/) - Understanding branches and PRs

</details>

### Setting Up Your Development Environment

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Gamemanuel/AluminumDecoded.git
   cd AluminumDecoded
   ```

2. **Open in Android Studio:**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned repository folder

3. **Sync Gradle:**
   - Wait for Android Studio to sync the project
   - Resolve any dependency issues if they arise

---

## Development Workflow

### Branching Strategy

We use a feature branch workflow. Here's how to contribute:

1. **Create a new branch** from `main` for your work:
   ```bash
   git checkout main
   git pull origin main
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes** in the appropriate directory:
   - Robot code goes in `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`
   - Keep changes focused and minimal

3. **Test your changes** thoroughly:
   - Build the project in Android Studio
   - Deploy to the robot (if applicable)
   - Test all affected OpModes

4. **Commit your changes** with clear messages:
   ```bash
   git add .
   git commit -m "Add descriptive commit message"
   ```

5. **Push your branch:**
   ```bash
   git push origin feature/your-feature-name
   ```

### Branch Naming Convention

Use descriptive branch names that indicate the purpose:

- `feature/autonomous-improvements` - New features
- `bugfix/drivetrain-issue` - Bug fixes
- `docs/update-readme` - Documentation updates
- `refactor/cleanup-teleopmode` - Code refactoring

---

## Pull Request Guidelines

> [!IMPORTANT]
> **Read this carefully if you're new to pull requests!**

### Before Creating a Pull Request

Make sure your PR:

- Has a clear, descriptive title
- Includes a detailed description of changes
- References any related issues
- Has been tested on the robot (for code changes)
- Doesn't include unnecessary files (build artifacts, IDE configs, etc.)

### Creating a Pull Request

1. **Navigate to the repository** on GitHub
2. **Click "Pull requests"** → **"New pull request"**
3. **Select your branch** as the compare branch
4. **Ensure the base branch is `main`** (not the FIRST upstream repository!)
5. **Fill out the PR template:**

   ```markdown
   ## Description
   Brief description of what this PR does

   ## Type of Change
   - [ ] Bug fix
   - [ ] New feature
   - [ ] Documentation update
   - [ ] Code refactoring

   ## Testing
   Describe how you tested these changes

   ## Screenshots (if applicable)
   Add screenshots of robot behavior or code changes
   ```

6. **Request reviews** from team members
7. **Address feedback** promptly

> [!WARNING]
> **Important:** Always make sure the base repository is `Gamemanuel/AluminumDecoded`, not the FIRST upstream repository. We don't want to accidentally submit our team code to the official FTC SDK!

<details>
<summary>Common Mistakes to Avoid</summary>

- **Don't** create a PR to the FIRST Tech Challenge repository
- **Don't** merge directly to `main` without a review
- **Don't** include personal configuration files or IDE settings
- **Don't** commit large binary files or build outputs
- **Don't** leave commented-out code or debug statements

</details>

---

## Reporting Issues

Found a bug? Have a feature request? Here's how to report it:

### Before Creating an Issue

- **Search existing issues** to avoid duplicates
- **Check documentation** to ensure it's actually a bug
- **Try to reproduce** the issue consistently

### Creating a Good Issue

Use our issue templates and include:

#### For Bug Reports:

```markdown
**Description:**
Clear description of the bug

**Steps to Reproduce:**
1. Step one
2. Step two
3. Step three

**Expected Behavior:**
What should happen

**Actual Behavior:**
What actually happens

**Environment:**
- Robot Controller version: [e.g., 9.0]
- Phone model: [e.g., Motorola Moto G]
- OpMode: [e.g., TeleOp.java]

**Logs/Screenshots:**
Attach relevant logs or screenshots
```

#### For Feature Requests:

```markdown
**Feature Description:**
Clear description of the proposed feature

**Use Case:**
Why is this feature needed?

**Proposed Implementation:**
How might this work? (optional)

**Alternatives Considered:**
What other approaches could work?
```

> [!TIP]
> Include logcat output for crashes! In Android Studio: View → Tool Windows → Logcat

---

## Release Process

### When to Create a Release

Create a new release when:

- Merging significant features to `main`
- Preparing for a competition
- Completing a major milestone
- After substantial bug fixes

### Release Format

Follow semantic versioning: `vMAJOR.MINOR.PATCH` (e.g., `v1.2.0`)

**Release notes template:**

```markdown
# Release v1.2.0 (2024-12-15)

## New Features
- Added improved autonomous path following
- Implemented new scoring mechanism for high basket

## Bug Fixes
- Fixed drivetrain drift issue (#12)
- Corrected IMU initialization bug (#15)

## Improvements
- Optimized TeleOp control responsiveness
- Updated motor power scaling for better precision

## Documentation
- Updated README with new setup instructions
- Added inline comments to complex algorithms

## Breaking Changes
- Changed motor configuration - requires re-mapping on Driver Station

## Contributors
@username1, @username2, @username3
```

### Creating a Release on GitHub

1. Go to **Releases** → **Draft a new release**
2. Create a new tag (e.g., `v1.2.0`)
3. Target the `main` branch
4. Use the template above for the description
5. Attach any relevant files (APKs, configuration files, etc.)
6. Publish the release

---

## Code Standards

### Java Code Style

- **Indentation:** 4 spaces (no tabs)
- **Naming Conventions:**
  - Classes: `PascalCase` (e.g., `DriveTrainController`)
  - Methods: `camelCase` (e.g., `moveForward()`)
  - Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_MOTOR_POWER`)
- **Comments:** Use JavaDoc for public methods
- **Organization:** Keep related functionality together

### Example:

```java
/**
 * Controls the robot's drivetrain using mecanum wheels
 */
public class MecanumDrive {
    private static final double MAX_POWER = 1.0;
    
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    
    /**
     * Drives the robot using mecanum kinematics
     * @param forward Forward/backward power (-1.0 to 1.0)
     * @param strafe Left/right power (-1.0 to 1.0)
     * @param rotate Rotation power (-1.0 to 1.0)
     */
    public void drive(double forward, double strafe, double rotate) {
        // Implementation here
    }
}
```

### File Organization

```
TeamCode/
└── src/main/java/org/firstinspires/ftc/teamcode/
    ├── autonomous/      # Autonomous OpModes
    ├── teleop/          # TeleOp OpModes
    ├── subsystems/      # Robot subsystem classes
    ├── util/            # Utility classes
    └── config/          # Configuration constants
```

---

## Resources

### Team Resources

- **Team Number:** 10523
- **Team Name:** The Dragons
- **Repository:** [Gamemanuel/AluminumDecoded](https://github.com/Gamemanuel/AluminumDecoded)

### FTC Resources

- [FTC SDK Documentation](https://github.com/FIRST-Tech-Challenge/FtcRobotController)
- [FTC Community Forums](http://ftcforum.firstinspires.org/forum.php)
- [Game Manual 1](https://www.firstinspires.org/resource-library/ftc/game-and-season-info)

### Learning Resources

- [Java Tutorial](https://docs.oracle.com/javase/tutorial/) - Official Java documentation
- [Android Development](https://developer.android.com/guide) - Android SDK guide
- [Git Branching](https://learngitbranching.js.org/) - Interactive Git visualization

---

## Getting Help

Having trouble? Here's how to get help:

1. **Check the documentation** in the `doc/` folder
2. **Search existing issues** on GitHub
3. **Ask team members** during meetings
4. **Create an issue** if you've found a bug or have a question

---

## License

This project is dual-licensed:

- **TeamCode folder:** [MIT License](https://github.com/Gamemanuel/AluminumDecoded/blob/main/LICENSE)
- **Root directory (FTC SDK):** [BSD 3-Clause License](https://github.com/Gamemanuel/AluminumDecoded/blob/main/LICENCE)

---

> [!NOTE]
> **Remember:** When in doubt, ask! We're all learning together. Don't be afraid to make mistakes – that's how we improve!