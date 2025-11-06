# CI/CD & Automation

This directory contains GitHub Actions workflows and automation configuration for the A.u.r.a.K.a.i Reactive Intelligence project.

## 🔄 Workflows

### CI Build & Test (`ci.yml`)
**Triggers:** Push to main/develop/claude/**, Pull Requests
- ✅ Builds debug APK
- ✅ Runs unit tests
- ✅ Runs instrumentation tests (PRs only)
- ✅ Generates lint reports
- ✅ Uploads build artifacts
- ✅ Builds release APK (main/develop only)

### PR Checks (`pr-checks.yml`)
**Triggers:** Pull Request events
- ✅ Validates PR title format (Conventional Commits)
- ✅ Checks PR size and complexity
- ✅ Scans for TODOs and FIXMEs
- ✅ Detects hardcoded secrets
- ✅ Reports APK size changes
- ✅ Runs Danger checks

### Security (`security.yml`)
**Triggers:** Push, Pull Requests, Weekly schedule
- 🔒 CodeQL security analysis
- 🔒 Secret scanning with TruffleHog
- 🔒 Dependency vulnerability review
- 🔒 OWASP dependency check (scheduled)
- 🔒 Android security analysis (MobSF)
- 🔒 Permissions audit

### Code Quality (`code-quality.yml`)
**Triggers:** Push, Pull Requests
- 📊 Ktlint formatting checks
- 📊 Detekt static analysis
- 📊 Android Lint
- 📊 SonarCloud analysis
- 📊 Code complexity reports

## 🤖 Dependabot

Automated dependency updates are configured in `dependabot.yml`:

- **Gradle dependencies:** Weekly updates (Monday 9 AM UTC)
- **GitHub Actions:** Monthly updates
- **Docker:** Monthly updates (if applicable)

### Dependency Groups
- AndroidX libraries
- Compose libraries
- Kotlin libraries
- Firebase libraries
- Testing libraries

## 🎯 PR Requirements

For a PR to be mergeable:
1. ✅ All CI checks must pass
2. ✅ Code review approval required
3. ✅ No merge conflicts
4. ✅ PR title follows Conventional Commits format
5. ✅ No critical security issues
6. ✅ No unresolved review comments

### Conventional Commit Types
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style/formatting
- `refactor`: Code refactoring
- `perf`: Performance improvements
- `test`: Test additions/changes
- `build`: Build system changes
- `ci`: CI/CD changes
- `chore`: Maintenance tasks
- `revert`: Revert previous commit

## 📦 Artifacts

Build artifacts are automatically uploaded and retained:
- **Build outputs:** 7 days
- **Test results:** 7 days
- **Lint reports:** 7 days
- **Release APKs:** 30 days
- **OWASP reports:** 30 days

## 🔐 Required Secrets

Configure these in repository settings:

### Signing (Optional)
- `SIGNING_KEY`: Base64-encoded keystore
- `ALIAS`: Key alias
- `KEY_STORE_PASSWORD`: Keystore password
- `KEY_PASSWORD`: Key password

### Services (Optional)
- `SONAR_TOKEN`: SonarCloud authentication
- `MOBSF_API_KEY`: MobSF API key for security analysis

## 🚀 Quick Start

### Running Checks Locally

```bash
# Run all checks
./gradlew check

# Lint
./gradlew ktlintCheck lintDebug

# Static analysis
./gradlew detekt

# Unit tests
./gradlew test

# Build
./gradlew assembleDebug
```

### Auto-fix Lint Issues

```bash
# Fix ktlint issues
./gradlew ktlintFormat

# Apply suggested Android Lint fixes
./gradlew lintFix
```

## 📊 Status Badges

Add these to your main README:

```markdown
![CI](https://github.com/AuraFrameFx/A.u.r.a.K.a.i_Reactive-Intelligence-/workflows/CI%20Build%20%26%20Test/badge.svg)
![Security](https://github.com/AuraFrameFx/A.u.r.a.K.a.i_Reactive-Intelligence-/workflows/Security%20Checks/badge.svg)
![Code Quality](https://github.com/AuraFrameFx/A.u.r.a.K.a.i_Reactive-Intelligence-/workflows/Code%20Quality/badge.svg)
```

## 🔧 Customization

### Adjusting Check Frequency

Edit workflow `schedule` cron expressions:
```yaml
schedule:
  - cron: '0 9 * * 1'  # Every Monday at 9 AM UTC
```

### Modifying Timeouts

Adjust `timeout-minutes` for jobs:
```yaml
jobs:
  build:
    timeout-minutes: 30  # Increase if builds take longer
```

### Changing Build Variants

Modify Gradle commands in workflow steps:
```yaml
- name: Build
  run: ./gradlew assembleDebug  # Change variant here
```

## 📚 Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Dependabot Documentation](https://docs.github.com/en/code-security/dependabot)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Android CI/CD Best Practices](https://developer.android.com/studio/projects/continuous-integration)
