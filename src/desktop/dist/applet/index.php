<?php
	include('../../includes/global.inc');
	$db = new DbAccess();
	
	//Obtém o nome da página.
	$requestURI = explode('/', $_SERVER['REQUEST_URI']);
	$pageName = $requestURI[count($requestURI) - 2];
	
	//Atualiza a contagem de acessos.
	$sql = 
		"UPDATE it_games
			SET views_count = views_count + 1
				, date_last_access = NOW()
		WHERE link = '".mysql_real_escape_string($pageName)."'";	
	$db->nonQuery($sql);
	
	//Obtém os dados do jogo.
	$sql = 
		"SELECT id_game
			, title
			, link
			, author
			, copyright
			, description
			, keywords
			, views_count
			, date_published
			, date_last_access
			FROM it_games
		WHERE link = '".mysql_real_escape_string($pageName)."'";
	$game = $db->getArray($sql);
	
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" 
   "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="pt-br">
    <head>
	<head>		
		<title><?php echo($game["title"]) ?></title>				
		
		<meta http-equiv="Content-Type" content="text/html;charset=iso-8859-1" />
		<meta http-equiv="Content-Language" content="pt-br" />
		
		<meta name="title" content="<?php echo($game["title"]) ?>" />
		<meta name="author" content="<?php echo($game["author"]) ?>" />
		<meta name="copyright" content="<?php echo($game["copyright"]) ?>" />
		<meta name="description" content="<?php echo($game["description"]) ?>" />
		<meta name="keywords" content="<?php echo($game["keywords"]) ?>" />
		
		<link rel="shortcut icon" href="icon.png" />
		<link rel="stylesheet" href="index.css" type="text/css" media="all" />
		
		<script type="text/javascript" src="jquery-1.6.2.min.js"></script>
		<script type="text/javascript">			
			$(document).ready(function() {
				$('#key').click(function() { 
					$('#key').fadeOut('slow', function() {
						$('#game').show();
					});
				});
			});
		</script>
	</head>
	<body>
		<img id="logo" src="images/logo.png" alt="Telyn" />
		<img id="key" src="images/key.png" alt="Telyn" />
		<div id="game">
			<applet code="org.lwjgl.util.applet.AppletLoader" archive="libs/lwjgl_util_applet.jar" codebase="." width="800" height="480">
				<param name="al_title" value="telyn" />
				<param name="al_main" value="org.newdawn.slick.AppletGameContainer" />
				<param name="al_version" value="0.1" />
				<param name="game" value="org.escape2team.telyn.states.Loader" />
				<param name="al_logo" value="logo.png" />
				<param name="al_progressbar" value="appletprogress.gif" />
				<param name="al_jars" value="telyn.jar, libs/core.jar, libs/jinput.jar, libs/jogg-0.0.7.jar, libs/jorbis-0.0.15.jar, libs/lwjgl.jar, libs/lwjgl_util.jar" />
				<param name="al_windows" value="libs/windows_natives.jar" />
				<param name="al_linux" value="libs/linux_natives.jar" />
				<param name="java_arguments" value="-Xmx256m -Dsun.java2d.noddraw=true" />
				<param name="separate_jvm" value="true" />
			</applet>
		</div>
		<p>
			2011 <strong>Escape2Team</strong> - 
			<a href="http://intentor.com.br/" target="_blank">André "Intentor" Martins</a> e 
			<a href="http://twitter.com/gamaroque/" target="_blank">Gabriel Roque</a> 
		</p>
	</body>
</html>