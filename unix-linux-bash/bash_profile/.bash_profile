# ~/.bash_profile: executed by bash(1) for login shells.

# User dependent .bash_profile file
# source the users bashrc if it exists
if [ -f "${HOME}/.bashrc" ] ; then
    source "${HOME}/.bashrc"
fi

# Set PATH so it includes user's private bin if it exists
if [ -d "${HOME}/bin" ] ; then
    PATH="${HOME}/bin:${PATH}"
fi

# Set MANPATH so it includes users' private man if it exists
if [ -d "${HOME}/man" ]; then
    MANPATH="${HOME}/man:${MANPATH}"
fi

# Set INFOPATH so it includes users' private info if it exists
if [ -d "${HOME}/info" ]; then
    INFOPATH="${HOME}/info:${INFOPATH}"
fi

# ALIASES
alias rm='rm -i'                                # Ask confirmation
alias cp='cp -i'                                # Ask confirmation
alias mv='mv -i'                                # Ask confirmation
alias df='df -h'                                # Human readable figures
alias du='du -h'                                # Human readable figures
alias less='less -r'                            # raw control characters
alias whence='type -a'                            # where, of a sort
alias grep='grep --color'                        # show differences in colour
alias egrep='egrep --color=auto'                # show differences in colour
alias fgrep='fgrep --color=auto'                # show differences in colour
alias ls='ls -h --color=tty'                    # classify files in colour
alias dir='ls --color=auto --format=vertical'
alias vdir='ls --color=auto --format=long'
alias ll='ls -Al'                                # long list
alias la='ls -A'                                # all but . and ..

# BASH PROMPT
# get current branch in git repo
function parse_git_branch() {
    BRANCH=`git branch 2> /dev/null | sed -e '/^[^*]/d' -e 's/* \(.*\)/\1/'`
    if [ ! "${BRANCH}" == "" ]
    then
        STAT=`parse_git_dirty`
        echo "[${BRANCH}${STAT}]"
    else
        echo ""
    fi
}
# get current status of git repo
function parse_git_dirty {
    status=`git status 2>&1 | tee`
    dirty=`echo -n "${status}" 2> /dev/null | grep "modified:" &> /dev/null; echo "$?"`
    untracked=`echo -n "${status}" 2> /dev/null | grep "Untracked files" &> /dev/null; echo "$?"`
    ahead=`echo -n "${status}" 2> /dev/null | grep "Your branch is ahead of" &> /dev/null; echo "$?"`
    newfile=`echo -n "${status}" 2> /dev/null | grep "new file:" &> /dev/null; echo "$?"`
    renamed=`echo -n "${status}" 2> /dev/null | grep "renamed:" &> /dev/null; echo "$?"`
    deleted=`echo -n "${status}" 2> /dev/null | grep "deleted:" &> /dev/null; echo "$?"`
    bits=''
    if [ "${renamed}" == "0" ]; then
        bits=">${bits}"
    fi
    if [ "${ahead}" == "0" ]; then
        bits="*${bits}"
    fi
    if [ "${newfile}" == "0" ]; then
        bits="+${bits}"
    fi
    if [ "${untracked}" == "0" ]; then
        bits="?${bits}"
    fi
    if [ "${deleted}" == "0" ]; then
        bits="x${bits}"
    fi
    if [ "${dirty}" == "0" ]; then
        bits="!${bits}"
    fi
    if [ ! "${bits}" == "" ]; then
        echo " ${bits}"
    else
        echo ""
    fi
}
export PS1="\[\e[32m\]\u\[\e[m\]\[\e[32m\]@\[\e[m\]\[\e[32m\]\h\[\e[m\] \[\e[33m\]\w\[\e[m\] \`parse_git_branch\`\n\\$ "

# COLORS
# Text color
echo -ne "\033]10;#C8C8C8\007"
# Background color
echo -ne "\033]11;#000000\007"

# PER-PLATFORM CONFIG
unameOut="$(uname -s)"
case "${unameOut}" in
    Linux*)
        # Config for LINUX
        machine=Linux
        
        ;;
    Darwin*)
        # Config for MAC
        machine=Mac
        
        ;;
    CYGWIN*)
        # Config for CYGWIN (Windows)
        machine=Cygwin
        # Aliases
        alias npp="/cygdrive/c/Program\ Files/Notepad++/notepad++.exe"
        # Startup directory, only if we are not opening bash on a specific path
        if [ `pwd` = $HOME ]; then
            cd /cygdrive/d/
        fi
        # Android studio fix
        if [ ! -z "${IDE}" -a "${IDE}" == "AndroidStudio" ]; then
            cd $OLDPWD;
        fi
        ;;
    MINGW*)
        # Config for MINGW (Windows GIT bash shell)
        machine=MinGw
        # Aliases
        alias npp="/c/Program\ Files/Notepad++/notepad++.exe"
        alias cdd="cd /d/dev/1.workspace"
        # Startup directory, only if we are not opening bash on a specific path
        if [ `pwd` = $HOME ] || [ `pwd` = "/" ]; then
            cd /d/
        fi
        # Android studio fix
        if [ ! -z "${IDE}" -a "${IDE}" == "AndroidStudio" ]; then
            cd $OLDPWD;
        fi
        ;;
    *)
        # Config for UNKNOWN / default
        machine="UNKNOWN:${unameOut}"
        
        ;;
esac
echo ${machine}
