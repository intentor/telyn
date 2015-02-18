AppTitle "SpriteMaster Pro - RotateSprite"

; NOTE: uses sprite\var[0] to control speed


Include "..\includes\smPro.bb"

Const sw=800,sh=600

; set up 3d display -> gfxmode/camera/sprite pivot
SpriteGraphics sw,sh,0,2
CameraClsMode spritecamera,False,True
ClearTextureFilters
ClsColor 50,160,170
SetFont LoadFont("Tahoma",16,1)

; load image pack and create single-surface sprites
pack1=LoadPack("..\packs\testpack.png")
numsprites=CountPackedImages(pack1)

sprite.smSprite=GetPackedSprite("porche",pack1)
HandleSprite sprite,sprite\width/4,sprite\height/2
PositionSprite sprite,sw/2,sh/2

road=GetPackedImage("road",pack1)


Repeat
	TileBlock road
	
	; wrap sprite around screen
	flag=False
	If sprite\x<-100 Then sprite\x=sw+100 : flag=True
	If sprite\x>sw+100 Then sprite\x=-100 : flag=True
	If sprite\y<-100 Then sprite\y=sh+100 : flag=True
	If sprite\y>sh+100 Then sprite\y=-100 : flag=True
	If flag PositionSprite sprite,sprite\x,sprite\y
	
	; change speed
	If KeyDown(200) sprite\var[0]=sprite\var[0]+0.5
	If KeyDown(208) sprite\var[0]=sprite\var[0]-0.5
	sprite\var[0]=sprite\var[0]/1.04

	; change direction (left/right)
	sprite\angle=sprite\angle+Float(KeyDown(205)-KeyDown(203))*(sprite\var[0]/4.6)
	
	; rotate and move the sprite
	RotateSprite sprite,sprite\angle,sprite\var[0]

	RenderWorld

	; show text messages		
	Color 255,255,255
	Text sw/2,5,"RotateSprite sprite,angle#[,movedistance#]",1
	Text 15,30,"This example uses the optional 'movedistance' parameter in RotateSprite"
	Text 15,50,"A positve value moves the sprite forward according to its angle"
	Text 15,70,"A negative value moves the sprite backward according to its angle"
	Text 10,105,"[Cursor LEFT/RIGHT] turn"
	Text 10,125,"[Cursor UP] move forward"
	Text 10,145,"[Cursor DOWN] move backward "
	Text 20,sh-40,"Angle="+Int(sprite\angle)
	Text 100,sh-40,"Speed="+sprite\var[0]
	Flip
Until KeyHit(1)

; cleanup
FreePack pack1
EndSpriteGraphics

End