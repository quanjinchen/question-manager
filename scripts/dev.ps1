param(
    # 要启动的前端应用：
    # admin 会同时启动后端 console 服务；
    # h5 会同时启动后端 auth 服务。
    [ValidateSet("admin", "h5")]
    [string]$Frontend = "admin"
)

# 脚本中任意命令出错时立即停止，避免后续继续启动出误导性的窗口。
$ErrorActionPreference = "Stop"

# 根据当前脚本所在目录反推出项目根目录。
$root = Split-Path -Parent $PSScriptRoot
$webDir = Join-Path $root "web"
$serverDir = Join-Path $root "server"

# 检查本机是否已经安装并能直接使用指定命令。
function Assert-Command {
    param(
        [string]$Name,
        [string]$InstallHint
    )

    if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
        throw "Command '$Name' was not found. $InstallHint"
    }
}

# 为每个服务打开一个独立 PowerShell 窗口，方便分别查看前端和后端日志。
function Start-DevProcess {
    param(
        [string]$Title,
        [string]$WorkingDirectory,
        [string]$Command
    )

    Write-Host "Starting $Title ..." -ForegroundColor Cyan
    $args = @(
        "-NoExit",
        "-ExecutionPolicy", "Bypass",
        "-Command",
        "Set-Location -LiteralPath '$WorkingDirectory'; $Command"
    )
    return Start-Process -FilePath "powershell" -ArgumentList $args -PassThru
}

# 前端依赖 pnpm，后端依赖 Maven。
Assert-Command "pnpm" "Install pnpm, or enable it with corepack."
Assert-Command "mvn" "Install Maven and make sure mvn is available in PATH."

# admin 对应前端 pnpm dev，h5 对应前端 pnpm dev:h5。
$frontendScript = if ($Frontend -eq "h5") { "dev:h5" } else { "dev" }

# admin 对应后端 console，h5 对应后端 auth。
$backendModule = if ($Frontend -eq "h5") { "auth" } else { "console" }
$backendTitle = if ($Frontend -eq "h5") { "backend auth service" } else { "backend console service" }
$backendJar = if ($Frontend -eq "h5") { ".\auth\target\smallauthplatform-auth-1.0.0.jar" } else { ".\console\target\smallAuthPlatform-1.0.0.jar" }
$backendCommand = "mvn -pl $backendModule -am -DskipTests package; if (`$LASTEXITCODE -eq 0) { java -jar $backendJar }"
$processes = @()

# 启动当前前端对应的后端服务：先构建模块 jar，再运行 jar。
$processes += Start-DevProcess $backendTitle $serverDir $backendCommand

# 启动选中的前端应用。
$processes += Start-DevProcess "frontend $Frontend app" $webDir "pnpm $frontendScript"

# 汇总常用访问地址。
Write-Host ""
Write-Host "Dev processes started from root." -ForegroundColor Green
if ($Frontend -eq "h5") {
    Write-Host "H5 frontend: http://localhost:5173"
    Write-Host "Auth API:    http://localhost:18081/auth"
} else {
    Write-Host "Admin frontend: http://localhost:5174"
    Write-Host "Console API:     http://localhost:18080/api"
}
Write-Host ""
Write-Host "Close the opened PowerShell windows to stop the services."


