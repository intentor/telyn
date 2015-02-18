; BMPFont - quick test
Include "BMPFont Include.bb"

Graphics 640,480,0,2
SetBuffer BackBuffer()

BMPFONT_Create("Arial",22,30,30,"test.bmp",1)
font=BMPFONT_Load("test.bmp",30,30)

ClsColor 120,120,120 : Cls

BMPFont_Print(25,25,"Testing Font ABC123",0,0,font)

Flip
WaitKey
End