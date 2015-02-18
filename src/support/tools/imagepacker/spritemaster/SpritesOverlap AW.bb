AppTitle "SpriteMaster Pro - Overlap #3"

; This modified example is by Aaron W.
; Uses pixel checking to determine collisions
; between cursor and sprites.

Include "..\includes\smPro.bb"

Const sw=800 , sh=600
SpriteGraphics sw,sh,32,2
CameraClsMode spritecamera,True,False
CameraClsColor spritecamera,160,170,190
SetFont LoadFont("tahoma",16,1)

; load the sprite pack
pack1=LoadPack("..\packs\testpack.png")
numSprites=CountPackedImages(pack1)

; grab all sprites from the pack
For n=1 To numSprites
	sprite.smSprite=GetPackedSprite(n,pack1)
Next
Gosub shuffle

cursor.smSprite=CreateSprite(1,1)
HandleSprite cursor
SpriteAlpha cursor,0

Repeat

	If KeyHit(57) Gosub shuffle

	PositionSprite cursor,MouseX(),MouseY()
	spritehitname$="nothing"
	For sprite.smSprite=Each smSprite
		If cursor<>sprite
			SpriteColor sprite ; reset color
			If SpritesOverlap(cursor,sprite)
				If PikPixel(sprite)
					SpriteColor sprite,155,55,155
					If spritehitname$="nothing"
						spritehitname$=sprite\name$
					Else
						spritehitname$=spritehitname$+", "+sprite\name$
					EndIf
				End If
			EndIf
		EndIf
	Next
	
	RenderWorld

	Color 0,0,0
	Text sw/2,10,"SpritesOverlap() - demo #2 using PIXEL checking",1
	Text sw/2,30,"Move cursor over images",1
	Text 20,70,"[SPACE] Shuffle images"
	Text 40,sh-30,"Cursor is overlapping {"+spritehitname$+"}"

	Flip
	
Until KeyHit(1)

; cleanup
FreePack pack1
FreeSprite cursor
EndSpriteGraphics
End

.shuffle
	For sprite.smSprite=Each smSprite
		sprite\x=Rand(20,sw-sprite\width-20)
		sprite\y=Rand(20,sh-sprite\height-20)
		PositionSprite sprite,sprite\x,sprite\y,Rand(200)
	Next
Return

;-----------------------------------------------------------------
Function PikPixel(Sprite.SmSprite)
	Local SX = Sprite\X
	Local Sy = Sprite\Y
	Local Col
	Local TX = Abs(MouseX()-Sx)
	Local TY = Abs(MouseY()-Sy)
	Local Img = GetPackedImage(Sprite\name$,Getpack(Sprite))
	Col = ReadPixel(TX,TY,ImageBuffer(img,Sprite\frame-1))
	FreeImage(img)
	Col2 = ( Blue(Col) + Green(Col) + Red(Col) )/3
	If col2<>0 
		Return True
	Else
		Return False
	End If 
End Function

Function RGB(r,g,b)
	Return r+(g Shl 8)+(b Shl 16)
End Function

Function Blue(hue)
	Return (hue And $00FF0000) Shr 16
End Function

Function green(hue)
	Return (hue And $0000FF00) Shr 8
End Function

Function Red(hue)
	Return (hue And $000000FF)
End Function