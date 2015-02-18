AppTitle "SpriteMaster Pro - HandleSprite"

; the include file
Include "..\includes\smPro.bb"

Const sw=800,sh=600

; set up 3d display -> gfxmode/camera/sprite pivot
SpriteGraphics sw,sh,0,2
CameraClsMode spritecamera,False,True
ClearTextureFilters
ClsColor 235,220,200
SetFont LoadFont("Tahoma",16,1)

; load pack and extract 'floppy' image
pack1=LoadPack("..\packs\testpack.png")
s.smSprite=GetPackedSprite("floppy",pack1)
;HandleSprite sprite

MoveMouse sw/2,sh/2
HidePointer

h=1 : mx=sw/2 : my=sh/2

Repeat
	Cls

	mxs=MouseXSpeed() : mys=MouseYSpeed()
	MoveMouse sw/2,sh/2
	
	If MouseDown(2)
		rot#=rot#+mxs/4
		RotateSprite s,rot#
	Else
		mx=mx+mxs : my=my+mys
	EndIf
	
	PositionSprite s,mx,my
	
	If KeyHit(57)
		h=h+1
		Select h
			Case 1 : HandleSprite s,0,0
			Case 2 : HandleSprite s,+s\width/2,0
			Case 3 : HandleSprite s,+s\width,0
			Case 4 : HandleSprite s,0,+s\height/2
			Case 5 : HandleSprite s
			Case 6 : HandleSprite s,+s\width,+s\height/2
			Case 7 : HandleSprite s,0,+s\height
			Case 8 : HandleSprite s,s\width/2,+s\height
			Case 9 : HandleSprite s,+s\width,+s\height : h=0
		End Select
	EndIf

	RenderWorld

	Color 255,0,255
	Line mx-20,my,mx+20,my
	Line mx,my-20,mx,my+20

	Color 0,0,0
	Text 10,10,"HandleSprite sprite,OffsetX,OffsetY"
	Text 20,40,"[SPACE] Move handle"
	Text 20,60,"[MOUSE] Move sprite"
	Text 20,80,"[MOUSE+RMB] Rotate sprite around handle"
	Text 30,sh-40,"Sprites Handle:   X offset="+s\handleX+"   Y offset="+s\handleY

	Flip
Until KeyHit(1)

; cleanup
FreePack pack1
EndSpriteGraphics

End