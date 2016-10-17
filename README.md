# KotlinJS Issue

### IDEA Application Setup - Run Server

From the IDEA menu do 'Run->Edit Configurations', click on '+' and select new 'Application' configuration:

Main class: io.vertx.core.Launcher

VM options: -Dvertx.disableFileCaching=true

Program arguments: run com.github.bmsantos.ApplicationVerticle --redeploy="src/main/**/*" --launcher-class=io.vertx.core.Launcher --on-redeploy="mvn resources:resources"

Working directory: /path/to/kotlinjs-issue