@import com.auo.juppy.ui.Page
@import gg.jte.Content


@param Page page
@param Content content

<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="description" content="${page.description()}">

    <title>${page.title()}</title>
</head>
<body>

${ page.text() }

${content}
</body>
</html>
