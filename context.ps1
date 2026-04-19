$nl = [Environment]::NewLine
$result = [System.Text.StringBuilder]::new()

git ls-files --cached --others --exclude-standard | ForEach-Object {
    $path = $_

    # Skip unwanted extensions
    $extCheck = [System.IO.Path]::GetExtension($path).ToLowerInvariant()
    if ($extCheck -in @('.jar', '.ps1', '.bat', '.cmd', '.exe', '.dll', '.so', '.dylib', '.zip', '.tar', '.gz', '.7z', '.rar', '.iso', '.img', '.bin', '.png', '.jpg', '.jpeg', '.gif', '.bmp', '.svg', '.webp', '.mp3', '.wav', '.ogg', '.flac', '.mp4', '.avi', '.mkv', '.mov', '.pdf', '.doc', '.docx', '.xls', '.xlsx', '.ppt', '.pptx', '.psd', '.ai', '.eps', '.ttf', '.otf', '.woff', '.woff2', '.eot', '.ico', '.lnk', '.tmp', '.log', '.bak', '.swp', '.swo', '.lock', '.DS_Store', 'thumbs.db', '.git', '.gitignore', '.gitattributes', '.gitmodules', '.gitkeep', '.gitkeep', '.editorconfig', '.eslintignore', '.prettierignore', '.stylelintignore', '.dockerignore', '.npmignore', '.yarnignore', '.hgignore', '.bzrignore', '.cvsignore', '.svnignore')) {
        return
    }

    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
        return
    }

    try {
        $content = [System.IO.File]::ReadAllText((Resolve-Path -LiteralPath $path))
    }
    catch {
        return
    }

    $ext = $extCheck.TrimStart('.')
    if ([string]::IsNullOrWhiteSpace($ext)) {
        $ext = 'txt'
    }

    [void]$result.Append($path)
    [void]$result.Append($nl)
    [void]$result.Append('```')
    [void]$result.Append($ext)
    [void]$result.Append($nl)
    [void]$result.Append($content)
    [void]$result.Append($nl)
    [void]$result.Append('```')
    [void]$result.Append($nl)
    [void]$result.Append($nl)
}

$result.ToString() | Set-Clipboard