<!DOCTYPE html>
<html lang="${project.language}">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<title>${project.title}</title>
<#list theme.stylesheets as stylesheet>
<link href="<@fika.relativize "${stylesheet}" />" rel="stylesheet" />
</#list>
</head>
<body>
<header class="navbar fixed-top navbar-dark">
<a class="navbar-brand" href="#">
<i class="fas fa-home fa-sm" aria-hidden="true"></i>
${project.title}</a>
</header>
<main class="container">${content?no_esc}</main>
<footer><p class="container">${project.copyright?no_esc}</p></footer>
<#list theme.scripts as script>
<script src="<@fika.relativize "${script}" />"></script>
</#list>
</body>
</html>
