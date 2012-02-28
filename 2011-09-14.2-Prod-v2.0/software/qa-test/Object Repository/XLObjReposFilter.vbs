Const xlXmlLoadImportToList = 2
Const xlXmlLoadOpenXml = 1
Const xlXmlLoadPromptUser = 0
'Const adOpenStatic = 3
'Const adLockOptimistic = 3
Dim XLObjRepository,objXLS,xlDB,XMLPath,TSRPath,xlFromXMLPath,objTSRPath,objXML
Dim rs,conn,sql2,XMLRepositoryObj,XLFilteredRepository
Set xlDB = CreateObject("Excel.Application")
Dim PathToParent,fso,CompletePathFound,objTSR,TestAUTPointer
Set XLObjRepository = New ExcelDBOperations
Dim qtApp
Set qtApp = CreateObject("QuickTest.Application")
If Not qtApp.Launched Then
	qtApp.Launch
End If

Set fso = CreateObject("Scripting.FileSystemObject")
TestAUTPointer = fso.GetAbsolutePathName(".")
Class ExcelDBOperations

Public Function CompletePath(ByRef ParentPath,ByVal strFullName)
Set PathToParent = fso.GetFolder(ParentPath)
If Not fso.FileExists(PathToParent &"\"& strFullName) Then
        For Each subFld in PathToParent.SubFolders
				If fso.FileExists(subFld.Path&"\"&strFullName) Then
					CompletePathFound = subFld.Path&"\"&strFullName									
				 Else
					CompletePathFound = CompletePath(subFld.Path,strFullName)					
			End If		
		Next	
		CompletePath = CompletePathFound
Else 
CompletePath = PathToParent &"\"& strFullName
End If

End Function

Public Function PathInitialize(AUTPointer)
	AUT = InputBox("Enter App Name:")
	objTSR = AUT&".tsr"
	objTSRPath = CompletePath(AUTPointer,objTSR)
	
	objXML = Replace(objTSRPath,".tsr",".xml")
	objXLS = Replace(objTSRPath,".tsr",".xls")
	
	If fso.FileExists(objXML) Then
		fso.DeleteFile(objXML)
	End If
	PathInitialize = objXLS
End Function

Public Function CreatePath(AUTPointer)
	CreatePath = AUTPointer&"\AUT.xls"
End Function

Public Function ExportTSRToXML(TSRPath,XMLPath)
Set XMLRepositoryObj = CreateObject("Mercury.ObjectRepositoryUtil")
	XMLRepositoryObj.ExportToXML TSRPath, XMLPath
	ExportTSRToXML = XMLPath
	Set XMLRepositoryObj = Nothing
End Function

Public Function ExcelDBConnectExec(byVal AUTPointer)
Set conn = CreateObject("ADODB.Connection")
Set rs = CreateObject("ADODB.RecordSet")
connstring = "Provider=Microsoft.Jet.OLEDB.4.0;" &_
					"Data Source=" & storeXL(AUTPointer) & ";" & _
					"Extended Properties=""Excel 8.0;HDR=Yes;"";"
					sql1 = "SELECT * INTO XLFilteredRepository FROM (SELECT Name AS Application,Name8 AS Page,Name22  AS Frame,Class37 AS ObjectType,Name38 AS ObjectNameOrKeyword FROM [Sheet1$] WHERE Name39='micclass' UNION ALL SELECT Name,Name8,'',Class21,Name22 FROM [Sheet1$] WHERE Class21 <> 'Frame' AND Name23 = 'micclass')"							
					'rs.CursorType = 3
					'rs.LockType = 3
					conn.Open connstring				
					Set rs = conn.Execute(sql1)
					conn.Close						
					Exit Function
End Function
Sub DropTables
xlDB.Workbooks.Open xlFromXMLPath
xlDB.DisplayAlerts = False
			 	i=1
				Do Until i=3
					xlDB.ActiveWorkbook.Sheets(i).Delete
					i=i+1
				Loop
										
xlDB.ActiveWorkbook.Save
xlDB.Workbooks.Close				
End Sub

Public Function storeXL(AUTPointer)
	xlFromXMLPath = PathInitialize(AUTPointer)
	xlDB.Workbooks.Add
	xlDB.Columns.AutoFit
	xlDB.Visible = False
	xlDB.AlertBeforeOverwriting = False
	xlDB.DisplayAlerts = False
	xlDB.Workbooks.OpenXML ExportTSRToXML(objTSRPath,objXML),,xlXmlLoadImportToList 'xlXmlLoadOpenXml
	xlDB.ActiveWorkbook.SaveAs xlFromXMLPath,True
	storeXL =CStr(xlFromXMLPath)
	xlDB.ActiveWorkbook.Close
End Function

End Class

XLObjRepository.ExcelDBConnectExec TestAUTPointer
XLObjRepository.DropTables


Wscript.Echo "Task Accomplished"

qtApp.Quit














































































































