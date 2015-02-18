AppTitle "SpriteMaster Pro Blitz Lights"

Include "..\includes\smPro.bb"

Const sw=640,sh=480

; set up 3d display -> gfxmode/camera/sprite pivot
SpriteGraphics sw,sh
CameraClsMode spritecamera,False,True
ClsColor 40,20,10
SetFont LoadFont("courier",18,1)

MoveMouse sw/2,sh/2


; lights
light1=CreateLight(3) : MoveEntity light1,-85,0,-10
PointEntity light1,spritecamera
LightColor light1,200,3,3
LightRange light1,150


; random cubes
For c=1 To 100
 cube=CreateCube()
 MoveEntity cube,Rnd(-20,20),Rnd(-20,20),30+Rnd(10)
Next

; load pack with 
pack1=LoadPack("..\packs\testpack.png")

sprite.smSprite=GetPackedSprite("floppy",pack1)
HandleSprite sprite
mesh=GetSpriteMesh(sprite)
EntityFX mesh,4


Repeat
	Cls
	
	If KeyHit(57)
	 LightColor light1,Rand(255),Rand(255),Rand(255)
	EndIf
	
	x=MouseX() : y=MouseY()
	PositionSprite sprite,x,y
	RenderWorld
	
	Text 10,10,"[SPACE] Change light color"
	Text 10,30,"[MOUSE] Move sprite around"
			
	Flip
Until KeyHit(1)

; cleanup
FreePack pack1
EndSpriteGraphics

End