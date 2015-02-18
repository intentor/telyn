AppTitle "SpriteMaster Pro - GetPackedTexture()"

Include "..\includes\smPro.bb"

; set up display
Graphics3D 640,480,0,2
SetBuffer BackBuffer()
cam=CreateCamera()

SeedRnd 1208

; load pack
pack1=LoadPack("..\packs\testpack.png")
numsprites=CountPackedImages(pack1)

piv=CreatePivot()

For c=1 To numsprites
	box=CreateCube(piv)
	tex=GetPackedTexture(c,pack1)   ; extract texture (via index number)
	EntityTexture box,tex           ; apply texture
	EntityFX box,1                  ; set cube to full bright
	FreeTexture tex                 ; free texture
	MoveEntity box,Rnd(-4,4),Rnd(-4,4),Rnd(-4,4)
Next

; free up pack (no longer needed)
FreePack pack1
MoveEntity piv,0,0,11

Repeat
	TurnEntity piv,0.2,0.3,0,0.4
	RenderWorld
	Text 10,10,"texture=GetPackedTexture(index/name$,pack)"
	Flip
Until KeyHit(1)

End