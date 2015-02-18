AppTitle "SpriteMaster Pro - Speed test"

Include "..\includes\smPro.bb"

Const sw=640,sh=480
Const numsprites=250

; set up 3d display -> gfxmode/camera/sprite pivot
SpriteGraphics sw,sh
CameraClsMode spritecamera,True,False
CameraClsColor spritecamera,100,180,240
SetFont LoadFont("Tahoma",18,1)

balloon.smSprite=LoadSprite("balloon.png")
Global s.smSprite

For num=1 To numsprites
	Gosub addsprite
Next

; loop
Repeat
	
	If KeyHit(17) Then w=Not w : WireFrame w
	
	count=count+1
	
	If count Mod 4=0
		If KeyDown(28)
			Gosub addsprite : num=num+1
		EndIf
	
		If KeyDown(14)
			s=Last smSprite
			If s<>Null Freesprite s : num=num-1
		EndIf
	EndIf
	
	For s=Each smSprite
		PositionSprite s,s\x,s\y+4
		RotateSprite s,s\angle+2
		If s\y>sh+60 PositionSprite s,s\x,-80
	Next
	
	RenderWorld
	
	; FPS timer
	If MilliSecs()>oldTime
	   oldTime=MilliSecs()+1000
	   fps=frameCount : frameCount=0
	Else
	   frameCount=frameCount+1
	EndIf
	
	Text 10,10,fps+" FPS"
	Text 20,50,"[RETURN] = Add sprites"
	Text 20,70,"[BACKSPACE] = Remove sprites"
	Text sw/2,sh-60,"Hold SPACE to turn off frame sync",1
	Text 10,sh-32,"Total sprites="+Str$(num-1)
	
	Flip Not KeyDown(57)
	
Until KeyHit(1)

; cleanup
FreeSprite balloon
EndSpriteGraphics
End

.addsprite
	s=CopySprite(balloon)
	SpriteColor s,Rand(255),Rand(255),Rand(255),Rnd(1)
	ResizeSprite s,Rand(20,80),Rand(20,80)
	RotateSprite s,Rand(360)
	HandleSprite s
	PositionSprite s,Rand(20,sw-20),Rand(-50,sh+50)
Return