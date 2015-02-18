AppTitle "SpriteMaster Pro - Animation"

; the include file
Include "..\includes\smPro.bb"

Const sw=400,sh=300

; set up 3d display -> gfxmode/camera/sprite pivot
SpriteGraphics sw,sh,0,2
CameraClsMode spritecamera,False,True
ClsColor 90,90,60

SetFont LoadFont("Courier",16,1)

; load pack and get runner animation
pack1=LoadPack("..\packs\testpack.png")
runner.smSprite=GetPackedSprite("runner",pack1)
PositionSprite runner,sw/2,sh/2,4

dir=1 ; animation direction   +1=forward -1=backward

Repeat
	Cls
	If KeyHit(57) dir=-dir
	If KeyHit(45) FlipSprite runner,True
	If dir=1 Then d$="(FORWARD)" Else d$="(BACKWARD)"
	count=count+1
	If count Mod 6=0
		PositionSprite runner,sw/2,sh/2,runner\frame+dir
	EndIf
	RenderWorld
	Color 255,255,255
	Text sw/2,5,"Animation example",1
	Text 20,70,"[SPACE] Change direction "+d$
	Text 20,90,"[X] Flip horizontally"
	Text sw/2,25,"PositionSprite sprite,x,y,frame",1
	Text 40,sh-35,"Frame: "+runner\frame+" of "+runner\numframes
	Flip
Until KeyHit(1)

; cleanup
FreePack pack1
EndSpriteGraphics

End