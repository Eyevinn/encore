# SVT Encore (Eyevinn fork)
This repository is forked from the upstream SVT Encore repo at https://github.com/svt/encore

The purpose of this fork is not to create a 'permanent' fork, but rather for it to work as an incubator
for new features and enable rapid development.

The intention is to keep the difference from upstream as small as possible by contributing
all new features back to upstream.

## Branches, versions and releases in this repo
### master branch
The master branch tracks the upstream master. No development should be done on this branch.

### Release branches
This fork uses release branches, where each version a.b.c from upstream forms the basis for a branch
`release-a.b.c` in this repository. New features/PR should be developed on the latest release branch.

### Release versioning
Releases in this repo follows a versioning scheme `a.b.c-d` where `a.b.c` would be the version of the
current upstream release, and `d` would start at 1 for each release branch and then be incremented
by one for each release from that branch.

## Creating a new release
1. Make sure all changes for the release are merged into the release branch
2. Run `./gradlew clean build` to make sure everything builds correctly
3. Tag the head of the release branch with the new version `a.b.c-d` (See [Release versioning](#release-versioning) above)
4. Push the tag to the remote repository. The project will be built and a release created automatically.
5. Edit the description of the release on GitHub to include relevant information about the release.

## Keeping in sync with upstream
When a new version is released in the upstream repository, new release branch based on the latest upstream release
should be created. This release branch should include all changes from the previous release branch that are not
included in the new upstream release. This can be done either by using `git rebase` or `git cherry-pick`,
both methods are describe below.


### New release branch with `git cherry-pick`
1. Pull the changes from upstream into this repositories `master` branch.
2. Create a new branch off of `master` called `release-a.b.c`, where `a.b.c` is the
   new upstream release version.
3. Use `git cherry-pick` to transfer all changes from the previous release branch that are not included
   in the new upstrem release to the new release branch
4. Create a new release `va.b.c-1`.

### New release branch with `git rebase`
1. Pull the changes from upstream into this repositories `master` branch.
2. Create a new branch off of the previous release branch called `release-a.b.c`, where `a.b.c` is the
   new upstream release version.
3. Use `git rebase` to reabse the new release branch onto `master`.
4. Create a new release `va.b.c-1`.

## Contributing to this fork
To contribute a new feature, bug fix etc based on this repository, follow the steps below.
Breaking changes, and changes to default behaviour should be avoided if possible.
Also see the [general contribution guidelines](CONTRIBUTING.adoc).

1. Create a branch from the lastest release branch
2. Implement your changes
3. Unless there is a specific reason not to, squash your changes into a single commit
4. Make sure your commit messages are in accordance with the [general contribution guidelines](CONTRIBUTING.adoc),
and make sure any new feature is documented with a description on how to use it, relevant configuration properties etc.
5. Create a PR against the release branch and ask someone to review it
6. Once approved, merge your branch. *Make sure to use "Squash and merge" to keep the commit history clean.*
7. Create a new pr branch from the `master` branch, named it with a `pr/` prefix, e.g. `pr/awesome-feature`. If the pr
    is related to an issue, include the issue number in the branch name, e.g. `pr/awesome-feature-123`.
8. Cherry-pick your changes onto that branch
9. Create a PR from this branch onto upstream.

### Commit messages
Commit messages should:
- follow the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification
- be as descriptive as possible
- If a new feature is added, the commit message should include details on how to use it,
relevant configuration properties etc.
