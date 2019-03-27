' название программы
Dim sAppName

' style = 0 - скрыть окно, 2 - свернуть, 3 - развернуть
Dim style

' doWait = false - не ждать завершения, true - ожидать завершения
Dim doWait

Set WS=WScript.CreateObject("WScript.Shell")
sAppName = "java MyServer"
style=2
doWait=false

WS.Run sAppName, style, doWait
WS=0