If %OS%'==Windows_NT' Set NTSwitch=/F "Tokens=*"
For %NTSwitch% %%V In (%1) Do set CLASSPATH=%CLASSPATH%;%%V


