' �������� ���������
Dim sAppName

' style = 0 - ������ ����, 2 - ��������, 3 - ����������
Dim style

' doWait = false - �� ����� ����������, true - ������� ����������
Dim doWait

Set WS=WScript.CreateObject("WScript.Shell")
sAppName = "java MyServer"
style=2
doWait=false

WS.Run sAppName, style, doWait
WS=0