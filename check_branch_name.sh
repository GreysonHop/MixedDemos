#!/usr/bin/env bash
LC_ALL=C

function error() {
    echo -e "\033[31m\033[01m$1\033[0m"
}

local_branch="$(git rev-parse --abbrev-ref HEAD)"

valid_branch_regex="^(feature|bugfix|improvement|library|prerelease|release|hotfix)\/[a-z0-9._-]+$"

message="Invalid branch name. \nBranch names must adhere to: $valid_branch_regex. \nRename your branch to a valid name and try again."

if [[ ! $local_branch =~ $valid_branch_regex ]]
then
    error "$message"
    exit 1
fi

exit 0
