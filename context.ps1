$nl = [Environment]::NewLine
$result = [System.Text.StringBuilder]::new()

git ls-files --cached --others --exclude-standard | ForEach-Object {
    $path = $_

    # Skip unwanted extensions
    $extCheck = [System.IO.Path]::GetExtension($path).ToLowerInvariant()
    if ($extCheck -in @('.gitattributes', '.gitignore', '.jar', '.ps1', '.png', '.jpg', '.jpeg', '.gif', '.bmp', '.svg', '.ico', '.pdf', '.zip', '.tar', '.gz', '.7z', '.exe', '.dll', '.bin', '.iso', '.mp3', '.mp4', '.avi', '.mkv', '.mov', '.wmv', '.flv', '.webm')) {
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