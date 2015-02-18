; File        - "smPro.bb" 
; Description - SpriteMaster Pro v1.01 (include)
; Author      - Jim Brown (JimB at www.blitzbasic.com)
; CREDITS:
; Rob Cummings  - General help/advice
; skidracer     - Pixies routine
; Anthony Flack - Inspiration

Const smEntityOrder%=-100  ; default entity order
Global spritepivot%        ; pivot handle - See SpriteGraphics / CreateSpritePivot()
Global spritecamera%       ; camera handle - See SpriteGraphics
Global rMesh%,rMeshSurf%   ; handles to entity for controlling rotated sprites

; stores definitions for each unique sprite in a pack
Type smDefinition
	Field name$              ; name of sprite
	Field x%,y%              ; location of sprite in texture (pixels)
	Field w%,h%              ; width/height of sprite in texture (pixels)
	Field framesH%,framesV%  ; number of anim frames horizontally and vertically
End Type

; holds mesh, surface, texture handles of a pack
Type smContainer
	Field mesh%,surf%,tex%       ; handles for mesh/surface/texture
	Field texW#,texH#            ; texures width and height
	Field numimages%             ; number of images in container
	Field def.smDefinition[255]  ; sprites definition structure
End Type

; main sprite handler - holds settings for each sprite
Type smSprite
	Field name$                ; name of sprite
	Field x#,y#                ; position on screen (pixels)
	Field width#,height#       ; dimensions on screen (pixels)
	Field handleX%,handleY%    ; axis/handle position
	Field angle#               ; angle of sprite (rotation) - 0 to 360
	Field flippedX%,flippedY%  ; TRUE = image is flipped (Horizontally/Vertically)
	Field frame%,numframes%    ; animation - current frame & total number of frames
	Field red%,green%,blue%    ; RGB color values (0 to 255)
	Field alpha#               ; alpha (0.0 to 1.0)
	Field var#[9]              ; spare user variables
	Field iv.smInternalVars    ; internal sprite variables  *DO NOT MODIFY*
	Field c.smContainer        ; sprites container          *DO NOT MODIFY*
End Type

; internal sprite vars
Type smInternalVars
	Field x#,y#,w#,h# , hX%,hY%
	Field a#,oa# , fX%,fY% , fW#,fH# , vert%,ws%,wt%
	Field u0#,v0# , u1#,v1# , u2#,v2# , u3#,v3#
End Type

; create a complete sprite display with camera/pivot
Function SpriteGraphics(gw%,gh%,d%=0,m%=0,pivotdist#=1.00001)
	Graphics3D gw,gh,d,m
	SetBuffer BackBuffer()
	spritecamera=CreateCamera()
	; create a default sprite pivot
	spritepivot=CreateSpritePivot(spritecamera,pivotdist)
	; mesh for rotating sprites
	rMesh=CreateMesh() : rMeshSurf=CreateSurface(rMesh)
	AddVertex rMeshSurf,0,0,0 : AddVertex rMeshSurf,0,0,0
	AddVertex rMeshSurf,0,0,0 : AddVertex rMeshSurf,0,0,0
	HideEntity rMesh : PositionEntity rMesh,-10000,-10000,0 
End Function

; free up all entites, brushes, textures, types, and close the 3d display
Function EndSpriteGraphics()
	ClearWorld : EndGraphics
	Delete Each smContainer : Delete Each smDefinition
	Delete Each smInternalVars : Delete Each smSprite
End Function

; creates a pivot in which to attach sprites to
Function CreateSpritePivot(cam%,dist#=1.00001)
	Local p%=CreatePivot(cam)
	NameEntity p,"SpriteMaster Pivot"
	Local aspect#=Float(GraphicsHeight())/Float(GraphicsWidth())
	Local scale#=2.0/Float(GraphicsWidth())
	PositionEntity p,-1,aspect,dist
	ScaleEntity p,scale,scale,scale
	Return p
End Function

; Use this command to zoom the main camera and sprite pivot
; NOTE: Only positive values of 1 and greater can be used
Function SpriteCameraZoom(cam%,z#)
	If z<1.0 z=1.00001
	If EntityClass$(cam)="Camera" CameraZoom cam,z
	Local numchilds%=CountChildren(cam),child%,c%
	If numchilds>0
		For c=1 To numchilds
			child=GetChild(cam,c)
			If EntityName$(child)="SpriteMaster Pivot"
				PositionEntity child,EntityX(child),EntityY(child),z
				Exit
			EndIf
		Next
	EndIf
End Function

; create a new sprite and pack (with animation)
Function CreateSprite.smSprite(width%,height%,framesH%=1,framesV%=1,flags%=0,par%=-1)
	If par=-1 par=spritepivot
	If flags<=0 flags=32+16+4+1
	If width<1 width=1
	If height<1 height=1
	; new sprite
	Local s.smSprite=New smSprite
	s\iv=New smInternalVars
	s\name$="sprite_"+Handle(s)
	s\iv\fW=width : s\iv\fH=height
	s\x=-10000 : s\y=-10000 : s\iv\x=-10000 : s\iv\y=-10000
	s\width=width*framesH : s\height=height*framesV : s\iv\w=s\width : s\iv\h=s\height
	s\frame=1 : s\numframes=framesH*framesV
	s\red=255 : s\green=255 : s\blue=255 : s\alpha=1.0
	; new container
	s\c=New smContainer
	s\c\mesh=CreateMesh(par)
	EntityFX s\c\mesh,1+2+8+16+32 : EntityOrder s\c\mesh,smEntityOrder%-1
	ScaleEntity s\c\mesh,1,-1,1 :	PositionEntity s\c\mesh,-0.5,-0.5,0
	s\c\surf=CreateSurface(s\c\mesh)
	s\c\texW=2 Shl (Len(Int(Bin(width*framesH-1)))-1) ; nearest power of 2
	s\c\texH=2 Shl (Len(Int(Bin(height*framesV-1)))-1) ; nearest power of 2
	s\c\tex=CreateTexture(s\c\texW,s\c\texH,flags)
	EntityTexture s\c\mesh,s\c\tex
	s\c\numimages=1
	; new definition
	s\c\def[0]=New smDefinition
	s\c\def[0]\w=width*framesH : s\c\def[0]\h=height*framesV
	s\c\def[0]\framesH=framesH : s\c\def[0]\framesV=framesV
	; uv coords
	s\iv\u0=0.0 : s\iv\v0=0.0
	s\iv\u1=s\iv\fW / s\c\texW : s\iv\v1=0.0
	s\iv\u2=0.0 : s\iv\v2=s\iv\fH / s\c\texH
	s\iv\u3=s\iv\fW / s\c\texW : s\iv\v3=s\iv\fH / s\c\texH
	; add quad
	AddVertex s\c\surf,s\iv\x,s\iv\y,0           , s\iv\u0,s\iv\v0
	AddVertex s\c\surf,s\iv\x+s\iv\fW,s\iv\y,0      , s\iv\u1,s\iv\v1
	AddVertex s\c\surf,s\iv\x,s\iv\y+s\iv\fH,0      , s\iv\u2,s\iv\v2
	AddVertex s\c\surf,s\iv\x+s\iv\fW,s\iv\y+s\iv\fH,0 , s\iv\u3,s\iv\v3
	VertexNormal s\c\surf,0,0,0,-1 : VertexNormal s\c\surf,1,0,0,-1
	VertexNormal s\c\surf,2,0,0,-1 : VertexNormal s\c\surf,3,0,0,-1
	AddTriangle s\c\surf,0,1,2 : AddTriangle s\c\surf,3,2,1
	Return s
End Function

; load an image and create a separate surface quad sprite
Function LoadSprite.smSprite(filename$,flags%=0,par%=-1)
	If flags<=0 flags=32+16+4+1
	If par=-1 par=spritepivot
	If FileType(filename$)=0 RuntimeError "LoadSprite() - file Not found:"+Chr$(10)+filename$
	Local img%=LoadImage(filename$)
	Local iw%=ImageWidth(img) , ih%=ImageHeight(img)
	Local tex%=LoadTexture(filename$,flags%)
	Local tw%=TextureWidth(tex) , th=TextureHeight(tex)
	Local s.smSprite=CreateSprite(iw,ih,1,1,flags,par)
	ScaleTexture tex,Float(iw)/Float(tw),Float(ih)/Float(th)
	FreeTexture s\c\tex : EntityTexture s\c\mesh,tex : s\c\tex=tex
	s\name$=sm_filename$(filename$)
	Return s
End Function

; duplicate an existing sprite (including size/color/alpha/rotation/flip status)
Function CopySprite.smSprite(s.smSprite)
	If s\iv\wt=2 Return ; TEXTSPRITE
	Local newspriteIndex% , ns.smSprite , o%
	ns.smSprite=GetPackedSprite(s\iv\ws+1,Handle(s\c))
	Local vS%=s\iv\vert , vD%=ns\iv\vert
	ns\name$=s\name$+"_"+Str$(vD)
	ns\iv\wt=s\iv\wt : ns\iv\hX=s\iv\hX : ns\iv\hY=s\iv\hY
	ns\handleX=ns\iv\hX : ns\handleY=ns\iv\hY
	ns\frame=s\frame
	FlipSprite ns,s\iv\fX,s\iv\fY
	ns\red=s\red : ns\green=s\green : ns\blue=s\blue : ns\alpha=s\alpha
	ns\iv\oa=-999999
	ResizeSprite ns,s\iv\w,s\iv\h
	HandleSprite ns,s\handleX,s\handleY
	RotateSprite ns,s\iv\a
	For o=0 To 3
		VertexColor ns\c\surf,vD+o,VertexRed(s\c\surf,vS+o),VertexGreen(s\c\surf,vS+o),VertexBlue(s\c\surf,vS+o),VertexAlpha(s\c\surf,vS+o)
	Next
	Return ns
End Function

; free up an existing sprite
Function FreeSprite(fs.smSprite)
	Local tMesh%=CopyMesh(fs\c\mesh)
	Local tSurf%=GetSurface(tMesh,1)
	Local numverts%=CountVertices(fs\c\surf)
	ClearSurface fs\c\surf
	Local vS%=0,vD%=0,o%,s.smSprite
	For s=Each smSprite
		If s\c=fs\c ; does sprite share container?
			If s=fs ; is this the sprite being freed?
				vS=vS+4
			Else
				For o=0 To 3
					AddVertex fs\c\surf,VertexX(tSurf,vS+o),VertexY(tSurf,vS+o),0 , VertexU(tSurf,vS+o),VertexV(tSurf,vS+o)
					VertexNormal fs\c\surf,o,0,0,-1
					VertexColor fs\c\surf,vD+o,VertexRed(tSurf,vS+o),VertexGreen(tSurf,vS+o),VertexBlue(tSurf,vS+o),VertexAlpha(tSurf,vS+o)
				Next
				AddTriangle fs\c\surf,vD+0,vD+1,vD+2
				AddTriangle fs\c\surf,vD+3,vD+2,vD+1
				s\iv\vert=vD : vS=vS+4 : vD=vD+4
			EndIf
		EndIf
	Next
	ClearSurface tSurf : FreeEntity tMesh
	If fs\iv\wt>=1 ; separate sprite / text sprite
		For o=0 To fs\c\numimages-1 : Delete fs\c\def[o] : Next
		FreeEntity fs\c\mesh
		If fs\iv\wt=1 FreeTexture fs\c\tex ; separate sprite
		Delete fs\c
	EndIf
	Delete fs
End Function

; load a pack of images
Function LoadPack(filename$,flags%=0)
	If flags<=0 flags=32+16+4+1
	If FileType(filename$)<>1 RuntimeError "LoadPack() - pack not found:"+Chr$(10)+filename$
	Local deffile$=Left$(filename$,Len(filename$)-4)+".def"
	If FileType(deffile$)<>1 RuntimeError "LoadPack() - no *.def file for pack:"+Chr$(10)+filename$
	Local tex%=LoadTexture(filename$,flags)
	If tex=0 RuntimeError "LoadPack()"+Chr$(10)+"failed to load texture:"+Chr$(10)+filename$
	; set up container
	Local	c.smContainer=New smContainer
	c\tex=tex : c\texW=TextureWidth(tex) : c\texH=TextureHeight(tex)
	; read the *.def file
	Local i%=0,file=ReadFile(deffile$)
	While Not Eof(file)
		If ReadLine$(file)="{"
			c\def[i]=New smDefinition
			c\def[i]\name$=Replace$(ReadLine$(file),Chr$(9),"")
			c\def[i]\x=Int(ReadLine$(file))
			c\def[i]\y=Int(ReadLine$(file))
			c\def[i]\w=Int(ReadLine$(file))
			c\def[i]\h=Int(ReadLine$(file))
			c\def[i]\framesH=Int(ReadLine$(file))
			c\def[i]\framesV=Int(ReadLine$(file))
			If c\def[i]\framesH<1 c\def[i]\framesH=1
			If c\def[i]\framesV<1 c\def[i]\framesV=1
			Local border$=ReadLine$(file)
			If border$<>"}"
				Local b=Int(ReadLine$(file))
				c\def[i]\x=c\def[i]\x+b
				c\def[i]\y=c\def[i]\y+b
				c\def[i]\w=c\def[i]\w-b*2
				c\def[i]\h=c\def[i]\h-b*2
			EndIf
			i=i+1
		EndIf
	Wend
	CloseFile file
	c\numimages=i
	Return Handle(c)
End Function

; free a previously loaded pack and associated sprites
Function FreePack(packIndex%)
	Local c.smContainer=Object.smContainer(packIndex)
	If c=Null RuntimeError "FreePack:"+Chr$(10)+"pack does not exist"
	Local s.smSprite,i%
	For s=Each smSprite
		If s\c=c ; if sprite container is same as referenced 'packIndex' container
			For i=0 To 99
				If s\c\def[i]<>Null Delete s\c\def[i] ; remove associated definition
			Next
			Delete s
		EndIf
	Next
	If c\surf<>0 ClearSurface c\surf
	FreeTexture c\tex
	If c\mesh<>0 FreeEntity c\mesh
	Delete c
End Function

; return pack used by a sprite
Function GetPack(s.smSprite)
	Return Handle(s\c)
End Function

; returns the number of images in a pack
Function CountPackedImages(packIndex%)
	Local c.smContainer=Object.smContainer(packIndex)
	If c=Null RuntimeError "CountPackedImages() - pack does not exist"
	Return c\numimages
End Function

; return name of an image from a pack via it's index number
Function PackedImageName$(Index%,packIndex%)
	Local c.smContainer=Object.smContainer(packIndex)
	Return c\def[index]\name$
End Function

; return number of animation frames in packed image
Function PackedImageFrames(ref$,packIndex%)
	Local c.smContainer=Object.smContainer(packIndex)
	If c=Null RuntimeError "CountFrames() - pack does not exist"
	Local i%=sm_findimage(ref$,c)
	If i=-1 RuntimeError "CountFrames() - image not in pack:"+Chr$(10)+ref$
	Return c\def[i]\framesH*c\def[i]\framesV
End Function

; get/create/extract a sprite from an existing pack
Function GetPackedSprite.smSprite(ref$,packIndex%,rx%=0,ry%=0,rw%=0,rh%=0)
	Local c.smContainer=Object.smContainer(packIndex)
	If c=Null RuntimeError "GetPackedSprite() - pack does not exist"
	Local v%,px#,py#
	Local i%=sm_findimage(ref$,c)
	If i=-1 RuntimeError "GetPackedSprite() - sprite not in pack:"+Chr$(10)+ref$
	; create new mesh/surface if not already in existance
	If c\mesh=0
		c\mesh=CreateMesh(spritepivot)
		c\surf=CreateSurface(c\mesh)
		EntityFX c\mesh,1+2+16+32 : EntityOrder c\mesh,smEntityOrder%
		ScaleEntity c\mesh,1,-1,1 : PositionEntity c\mesh,-0.5,-0.5,0
		EntityTexture c\mesh,c\tex
	EndIf
	; set up new sprite
	v=CountVertices(c\surf)
	Local s.smSprite=New smSprite
	s\iv=New smInternalVars
	s\name$=c\def[i]\name$
	s\iv\ws=i : s\c=c : s\iv\vert=v
	s\x=-10000 : s\y=-10000 : s\iv\x=-10000 : s\iv\y=-10000
	s\iv\fW=c\def[i]\w / c\def[i]\framesH : s\iv\fH=c\def[i]\h / c\def[i]\framesV
	s\width=s\iv\fW : s\height=s\iv\fH : s\iv\w=s\width : s\iv\h=s\height
	s\frame=1 : s\numframes=c\def[i]\framesH*c\def[i]\framesV
	px=c\def[i]\x : py=c\def[i]\y
	s\red=255 : s\green=255 : s\blue=255 : s\alpha=1.0
	; optional region area
	If rx>0 px=px+rx
	If ry>0 py=py+ry
	If rw>0 Then s\iv\fW=rw : s\width=rw : s\iv\w=s\width
	If rh>0 Then s\iv\fH=rh : s\height=rh : s\iv\h=s\height
	; set UV texture coords
	s\iv\u0=px / s\c\texW           : s\iv\v0=py / s\c\texH
	s\iv\u1=(px+s\iv\fW) / s\c\texW : s\iv\v1=py / s\c\texH
	s\iv\u2=px / s\c\texW           : s\iv\v2=(py+s\iv\fH) / s\c\texH
	s\iv\u3=(px+s\iv\fW) / s\c\texW : s\iv\v3=(py+s\iv\fH) / s\c\texH
	; create a set of quad verts
	AddVertex s\c\surf,-10000,-10000,0                 , s\iv\u0,s\iv\v0
	AddVertex s\c\surf,-10000+s\iv\fW,-10000,0         , s\iv\u1,s\iv\v1
	AddVertex s\c\surf,-10000,-10000+s\iv\fH,0         , s\iv\u2,s\iv\v2
	AddVertex s\c\surf,-10000+s\iv\fW,-10000+s\iv\fH,0 , s\iv\u3,s\iv\v3
	VertexNormal s\c\surf,v+0,0,0,-1 : VertexNormal s\c\surf,v+1,0,0,-1
	VertexNormal s\c\surf,v+2,0,0,-1 : VertexNormal s\c\surf,v+3,0,0,-1
	AddTriangle s\c\surf,v+0,v+1,v+2
	AddTriangle s\c\surf,v+3,v+2,v+1
	Return s
End Function

; gets a standard 2D (animated) image from a pack
Function GetPackedImage(ref$,packIndex%)
	Local c.smContainer=Object.smContainer(packIndex)
	If c=Null RuntimeError "GetPackedImage() - pack does not exist"
	Local img%,px%,py%,iw%,ih%,frame%=0
	Local i%=sm_findimage(ref$,c)
	If i=-1 RuntimeError "GetPackedImage() - image not in pack:"+Chr$(10)+ref$
	iw=c\def[i]\w / c\def[i]\framesH : ih=c\def[i]\h / c\def[i]\framesV
	px=c\def[i]\x : py=c\def[i]\y
	If c\def[i]\framesH*c\def[i]\framesV>1 ; animated
		img=CreateImage(iw,ih,c\def[i]\framesH*c\def[i]\framesV)
		For y=0 To c\def[i]\framesV-1
			For x=0 To c\def[i]\framesH-1
				CopyRect px+(x*iw),py+(y*ih),iw,ih , 0,0 , TextureBuffer(c\tex),ImageBuffer(img,frame)
				frame=frame+1
			Next
		Next
	Else ; non-animated
		img=CreateImage(iw,ih)
		CopyRect px,py,iw,ih , 0,0 , TextureBuffer(c\tex),ImageBuffer(img)
	EndIf
	Return img
End Function

; gets a texture from a pack
Function GetPackedTexture(ref$,packIndex%)
	Local c.smContainer=Object.smContainer(packIndex)
	If c=Null RuntimeError "GetPackedTexture() - pack does not exist"
	Local px%,py%,iw%,ih%
	Local i%=sm_findimage(ref$,c)
	If i=-1 RuntimeError "GetPackedTexture() - texture not in pack:"+Chr$(10)+ref$
	iw=c\def[i]\w / c\def[i]\framesH : ih=c\def[i]\h / c\def[i]\framesV
	px=c\def[i]\x : py=c\def[i]\y
	Local tex%=CreateTexture(iw,ih,32+16+8+4+1)
	CopyRect px,py,iw,ih , 0,0 , TextureBuffer(c\tex),TextureBuffer(tex)
	ScaleTexture tex,Float(TextureWidth(tex))/Float(iw),Float(TextureHeight(tex))/Float(ih)
	Return tex
End Function

; return number of sprites using the specifed pack
Function CountSpritesUsingPack(packIndex%)
	Local c.smContainer=Object.smContainer(packIndex)
	If c=Null RuntimeError "CountSpritesUsingPack() - pack does not exist"
	Local s.smSprite,num%=0
	For s=Each smSprite
		If s\c=c num=num+1
	Next
	Return num
End Function

; create a special 'text' sprite
Function CreateTextSprite.smSprite(filename$,offs%=0,flags%=0)
	If flags<=0 flags=32+16+4+1
	If FileType(filename$)<>1 RuntimeError "CreateTextSprite() - file not found:"+Chr$(10)+filename$
	Local deffile$=Left$(filename$,Len(filename$)-4)+".def"
	If FileType(deffile$)<>1 RuntimeError "CreateTextSprite() - no associated *.def file:"+Chr$(10)+filename$
	Local packIndex%=LoadPack(filename$,flags)
	Local s.smSprite=GetPackedSprite(1,packIndex)
	s\iv\wt=2 ; TEXTSPRITE
	EntityOrder s\c\mesh,smEntityOrder%-2
	s\iv\fX=offs ; offset character
	Return s
End Function

; clears all characters from a 'text' sprite
Function ClearText(s.smSprite)
	If s\iv\wt<>2 RuntimeError "ClearText -  Only works on 'text' sprites"
	ClearSurface s\c\surf
	s\width=0 : s\height=0 : s\iv\w=0 : s\iv\h=0
End Function

; add text/characters to a 'text' sprite
Function AddText(s.smSprite,x%,y%,txt$,vr%=255,vg%=255,vb%=255,va#=1.0)
	If s\iv\wt<>2 RuntimeError "AddText -  Only works on 'text' sprites"
	If txt$="" Return
	Local framesH%=s\c\def[s\iv\ws]\framesH
	Local l$,oX#,oY#,t%,frame%,mx%
	s\iv\x=x : s\iv\y=y
	mx=x
	Local numverts=CountVertices(s\c\surf)
	Local v=numverts
	; add text characters (create quad for each char)
	For t=1 To Len(txt$)
		l$=Mid$(txt$,t,1)
		If l$=Chr$(10) Or l$=Chr$(13) ; newline/carriage return
			x=s\iv\x-s\iv\hX : y=y+s\iv\fH
		ElseIf Asc(l$)=32 ; space
			x=x+s\iv\fW
		Else
			frame=Asc(l$)-33+s\iv\fX ; offset
			oY#=(frame / framesH) : oX#=(frame-(oY*framesH))
			oX#=(oX*s\iv\fW) / s\c\texW : oY#=(oY*s\iv\fH) / s\c\texH
			AddVertex s\c\surf,x,y,0                 , s\iv\u0+oX+0.001 , s\iv\v0+oY+0.001
			AddVertex s\c\surf,x+s\iv\fW,y,0         , s\iv\u1+oX-0.001 , s\iv\v1+oY+0.001
			AddVertex s\c\surf,x,y+s\iv\fH,0         , s\iv\u2+oX+0.001 , s\iv\v2+oY-0.001
			AddVertex s\c\surf,x+s\iv\fW,y+s\iv\fH,0 , s\iv\u3+oX-0.001 , s\iv\v3+oY-0.001
			AddTriangle s\c\surf,v+0,v+1,v+2
			AddTriangle s\c\surf,v+3,v+2,v+1
			v=v+4 : x=x+s\iv\fW : If x>mx Then mx=x
		EndIf
	Next
	For v=numverts To CountVertices(s\c\surf)-1
		VertexColor s\c\surf,v,vr,vg,vb,va
		VertexNormal s\c\surf,v,0,0,-1
	Next
	s\width=(mx-s\iv\x) : s\height=(y-s\iv\y+s\iv\fH)
	s\iv\w=s\width : s\iv\h=s\height
End Function

; position sprite on screen (and set animation frame)
Function PositionSprite(s.smSprite,newposX#,newposY#,frame%=-99999)
	Local offsX#=newposX-s\iv\x,offsY#=newposY-s\iv\y,numverts%=4,o%
	s\x=newposX : s\y=newposY : s\iv\x=newposX : s\iv\y=newposY
	If s\iv\wt=2 numverts=CountVertices(s\c\surf) ; TEXTSPRITE
	For o=0 To numverts-1
		VertexCoords s\c\surf,s\iv\vert+o,VertexX(s\c\surf,s\iv\vert+o)+offsX,VertexY(s\c\surf,s\iv\vert+o)+offsY,0
	Next
	; animate
	If frame<>-99999 And s\iv\wt<>2 ; TEXTSPRITE
		Local nf= s\c\def[s\iv\ws]\framesH * s\c\def[s\iv\ws]\framesV
		If nf>1
			Local framesH%=s\c\def[s\iv\ws]\framesH
			If frame>nf frame=(frame Mod nf)
			If frame<1 frame=nf-(Abs(frame) Mod nf)
			s\frame=frame : frame=frame-1
			Local oY#=(frame / framesH)
			Local oX#=(frame-(oY*framesH))
			oX#=(oX*s\iv\fW)/s\c\texW : oY#=(oY*s\iv\fH)/s\c\texH
			VertexTexCoords s\c\surf,s\iv\vert+0 , s\iv\u0+oX , s\iv\v0+oY
			VertexTexCoords s\c\surf,s\iv\vert+1 , s\iv\u1+oX , s\iv\v1+oY
			VertexTexCoords s\c\surf,s\iv\vert+2 , s\iv\u2+oX , s\iv\v2+oY
			VertexTexCoords s\c\surf,s\iv\vert+3 , s\iv\u3+oX , s\iv\v3+oY
		EndIf
	EndIf
End Function

; rotate a sprite by amount in angle
; move sprite by 'movedist' amount in current direction of rotation
Function RotateSprite(s.smSprite,angle#=0,movedist#=0)
	If s\iv\wt=2 Return ; TEXTSPRITE
	If angle<0.0 angle=360.0-(Abs(angle) Mod 360)
	If angle>360.0 angle=(angle Mod 360.0)
	s\angle=angle : s\iv\a=angle
	Local sprX#=s\iv\x+Cos(s\angle)*movedist,sprY#=s\iv\y+Sin(s\angle)*movedist,o%
	If (s\angle<>s\iv\oa) Or movedist<>0
		VertexCoords rMeshSurf,0 , 0-s\iv\hX,0-s\iv\hY,0
		VertexCoords rMeshSurf,1 , s\iv\w-s\iv\hX,0-s\iv\hY,0
		VertexCoords rMeshSurf,2 , 0-s\iv\hX,s\iv\h-s\iv\hY,0
		VertexCoords rMeshSurf,3 , s\iv\w-s\iv\hX,s\iv\h-s\iv\hY,0
		If s\angle<>0 RotateMesh rMesh,0,0,s\angle
		For o=0 To 3
			VertexCoords s\c\surf,s\iv\vert+o,VertexX(rMeshSurf,o),VertexY(rMeshSurf,o),0
		Next
		s\iv\oa=s\angle : s\iv\x=0 : s\iv\y=0
		PositionSprite s,sprX,sprY
	EndIf
End Function

; resize the sprite to new width/height screen pixels
Function ResizeSprite(s.smSprite,w%=0,h%=0)
	If s\iv\wt=2 Return ; TEXTSPRITE
	If w<=0 w=s\iv\fW
	If h<=0 h=s\iv\fH
	s\iv\hX=s\iv\hX*(Float(w)/s\iv\w)
	s\iv\hY=s\iv\hY*(Float(h)/s\iv\h)
	s\width=w : s\height=h : s\iv\w=s\width : s\iv\h=s\height
	VertexCoords s\c\surf,s\iv\vert+0 , s\iv\x+0-s\iv\hX,s\iv\y+0-s\iv\hY,0
	VertexCoords s\c\surf,s\iv\vert+1 , s\iv\x+w-s\iv\hX,s\iv\y+0-s\iv\hY,0
	VertexCoords s\c\surf,s\iv\vert+2 , s\iv\x+0-s\iv\hX,s\iv\y+h-s\iv\hY,0
	VertexCoords s\c\surf,s\iv\vert+3 , s\iv\x+w-s\iv\hX,s\iv\y+h-s\iv\hY,0
	s\handleX=s\iv\hX : s\handleY=s\iv\hY
	s\iv\oa=-999999 : RotateSprite s,s\iv\a
End Function

; flip the sprite horizontally / vertically
Function FlipSprite(s.smSprite,flipX%=False,flipY%=False)
	If s\iv\wt=2 Return ; TEXTSPRITE
	Local tc#
	If flipX
		s\iv\fX=Not s\iv\fX
		tc#=s\iv\u0 : s\iv\u0=s\iv\u1 : s\iv\u1=tc#
		tc#=s\iv\u2 : s\iv\u2=s\iv\u3 : s\iv\u3=tc#
		tc#=s\iv\v0 : s\iv\v0=s\iv\v1 : s\iv\v1=tc#
		tc#=s\iv\v2 : s\iv\v2=s\iv\v3 : s\iv\v3=tc#
		VertexTexCoords s\c\surf,s\iv\vert+0 , s\iv\u0 , s\iv\v0
		VertexTexCoords s\c\surf,s\iv\vert+1 , s\iv\u1 , s\iv\v1
		VertexTexCoords s\c\surf,s\iv\vert+2 , s\iv\u2 , s\iv\v2
		VertexTexCoords s\c\surf,s\iv\vert+3 , s\iv\u3 , s\iv\v3
	EndIf
	If flipY
		s\iv\fY=Not s\iv\fY
		tc#=s\iv\u0 : s\iv\u0=s\iv\u2 : s\iv\u2=tc#
		tc#=s\iv\u1 : s\iv\u1=s\iv\u3 : s\iv\u3=tc#
		tc#=s\iv\v0 : s\iv\v0=s\iv\v2 : s\iv\v2=tc#
		tc#=s\iv\v1 : s\iv\v1=s\iv\v3 : s\iv\v3=tc#
		VertexTexCoords s\c\surf,s\iv\vert+0 , s\iv\u0 , s\iv\v0
		VertexTexCoords s\c\surf,s\iv\vert+1 , s\iv\u1 , s\iv\v1
		VertexTexCoords s\c\surf,s\iv\vert+2 , s\iv\u2 , s\iv\v2
		VertexTexCoords s\c\surf,s\iv\vert+3 , s\iv\u3 , s\iv\v3
	EndIf
	s\flippedX=s\iv\fX : s\flippedY=s\iv\fY
End Function

; set the sprites axis/handle position
Function HandleSprite(s.smSprite,hx%=-99999999,hy%=-99999999)
	If s\iv\wt=2 Return ; TEXTSPRITE
	Local currentRot#=s\iv\a
	RotateSprite s,0
	If hx=-99999999 hx=s\iv\w/2
	If hy=-99999999 hy=s\iv\h/2
	Local newHX%=hx-s\handleX,newHY%=hy-s\handleY,o%
	s\iv\hX=hx : s\iv\hY=hy :	s\handleX=hx : s\handleY=hy
	For o=0 To 3
		VertexCoords s\c\surf,s\iv\vert+o,VertexX(s\c\surf,s\iv\vert+o)-newHX,VertexY(s\c\surf,s\iv\vert+o)-newHY,0
	Next
	RotateSprite s,currentRot
End Function

; set multiple sprite parameters > sprite frame pos [size] [rotation] [color] [alpha]
Function SetSprite(s.smSprite,frame%,x#,y#,w#=0,h#=0,rot#=0,r%=-1,g%=-1,b%=-1,a#=-1)
If s\iv\wt=2 Return ; TEXTSPRITE
	If frame=0 frame=1
	PositionSprite s,x,y,frame
	If (w>0 And h>0) ResizeSprite s,w,h
	If rot<>0 RotateSprite s,rot
	If r<0 r=VertexRed(s\c\surf,s\iv\vert)
	If g<0 g=VertexGreen(s\c\surf,s\iv\vert)
	If b<0 b=VertexBlue(s\c\surf,s\iv\vert)
	If a<0 a=VertexAlpha(s\c\surf,s\iv\vert)
	SpriteColor s,r,g,b,a
End Function

; change the sprites image reference
Function ChangeSprite(s.smSprite,ref$,rx%=0,ry%=0,rw%=0,rh%=0)
	If s\iv\wt=2 Return ; TEXTSPRITE
	Local c.smContainer=Object.smContainer(packIndex)
	If c=Null	c=s\c
	Local v%,px#,py#
	Local i%=sm_findimage(ref$,c)
	If i=-1 RuntimeError "ChangeSprite - sprite not in pack:"+Chr$(10)+ref$
	;
	Local hpx#=Float(s\handleX) / s\width , hpy#=Float(s\handleY) / s\height
	Local fx%=s\flippedX , fy%=s\flippedY
	s\name$=c\def[i]\name$
	s\iv\wt=0 : s\iv\ws=i
	s\iv\ws=i : s\c=c : s\iv\vert=v
	s\frame=1 : s\numframes=c\def[i]\framesH*c\def[i]\framesV
	s\iv\fW=c\def[i]\w / c\def[i]\framesH : s\iv\fH=c\def[i]\h / c\def[i]\framesV
	s\width=s\iv\fW : s\height=s\iv\fH : s\iv\w=s\iv\fW : s\iv\h=s\iv\fH
	ResetSprite s
	px=c\def[i]\x : py=c\def[i]\y
	; optional region area
	If rx>0 px=px+rx
	If ry>0 py=py+ry
	If rw>0 Then s\iv\fW=rw : s\width=rw : s\iv\w=s\width
	If rh>0 Then s\iv\fH=rh : s\height=rh : s\iv\h=s\height
	; set UV texture coords
	s\iv\u0=px / s\c\texW           : s\iv\v0=py / s\c\texH
	s\iv\u1=(px+s\iv\fW) / s\c\texW : s\iv\v1=py / s\c\texH
	s\iv\u2=px / s\c\texW           : s\iv\v2=(py+s\iv\fH) / s\c\texH
	s\iv\u3=(px+s\iv\fW) / s\c\texW : s\iv\v3=(py+s\iv\fH) / s\c\texH
	VertexTexCoords s\c\surf,s\iv\vert+0 , s\iv\u0 , s\iv\v0
	VertexTexCoords s\c\surf,s\iv\vert+1 , s\iv\u1 , s\iv\v1
	VertexTexCoords s\c\surf,s\iv\vert+2 , s\iv\u2 , s\iv\v2
	VertexTexCoords s\c\surf,s\iv\vert+3 , s\iv\u3 , s\iv\v3
	HandleSprite s,s\width*hpx,s\height*hpy
	FlipSprite s,fx,fy
End Function

; reset the sprite to default
Function ResetSprite(s.smSprite)
	s\handleX=0 :	s\handleY=0 : s\iv\hX=0 : s\iv\hY=0 :	s\frame=1
	FlipSprite s,s\iv\fX,s\iv\fY
	s\iv\oa=0 : s\angle=0 : s\iv\a=0
	RotateSprite s : ResizeSprite s : SpriteColor s
End Function

; changes sprites color/alpha
Function SpriteColor(s.smSprite,r%=-1,g%=255,b%=255,a#=-0.1)
	If a=-0.1 a=VertexAlpha(s\c\surf,s\iv\vert)
	If r=-1 Then r=255 : a=1
	Local numverts%=4,o%
	If s\iv\wt=2 numverts=CountVertices(s\c\surf) ; TEXTSPRITE
	For o=0 To numverts-1
		VertexColor s\c\surf,s\iv\vert+o , r,g,b , a
	Next
	s\red=r : s\green=g : s\blue=b : s\alpha=a
End Function

; change sprites alpha
Function SpriteAlpha(s.smSprite,a#=1)
	Local numverts%=4,o%
	If s\iv\wt=2 numverts=CountVertices(s\c\surf) ; TEXTSPRITE
	For o=0 To numverts-1
		VertexColor s\c\surf,s\iv\vert+o , VertexRed(s\c\surf,s\iv\vert),VertexGreen(s\c\surf,s\iv\vert),VertexBlue(s\c\surf,s\iv\vert) , a
	Next
	s\alpha=a
End Function

; returns TRUE if 2 sprites are overlapping
Function SpritesOverlap(s1.smSprite,s2.smSprite)
	Local x1%,y1%,x2%,y2%,x3%,y3%,x4%,y4% , o%
	; test sprite 1 against sprite 2
	x1=VertexX(s2\c\surf,s2\iv\vert+0) :	x2=VertexX(s2\c\surf,s2\iv\vert+1)
	x3=VertexX(s2\c\surf,s2\iv\vert+2) :	x4=VertexX(s2\c\surf,s2\iv\vert+3)
	y1=VertexY(s2\c\surf,s2\iv\vert+0) :	y2=VertexY(s2\c\surf,s2\iv\vert+1)
	y3=VertexY(s2\c\surf,s2\iv\vert+2) :	y4=VertexY(s2\c\surf,s2\iv\vert+3)
	For o=0 To 3
		pX=VertexX(s1\c\surf,s1\iv\vert+o)
		pY=VertexY(s1\c\surf,s1\iv\vert+o)	
		If (x2-x1)*(pY-y2)-(pX-x2)*(y2-y1)>0
			If (x4-x2)*(pY-y4)-(pX-x4)*(y4-y2)>0
				If (x3-x4)*(pY-y3)-(pX-x3)*(y3-y4)>0
					If (x1-x3)*(pY-y1)-(pX-x1)*(y1-y3)>0
						Return True
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	; test sprite 2 against sprite 1
	x1=VertexX(s1\c\surf,s1\iv\vert+0) :	x2=VertexX(s1\c\surf,s1\iv\vert+1)
	x3=VertexX(s1\c\surf,s1\iv\vert+2) :	x4=VertexX(s1\c\surf,s1\iv\vert+3)
	y1=VertexY(s1\c\surf,s1\iv\vert+0) :	y2=VertexY(s1\c\surf,s1\iv\vert+1)
	y3=VertexY(s1\c\surf,s1\iv\vert+2) :	y4=VertexY(s1\c\surf,s1\iv\vert+3)
	For o=0 To 3
		pX=VertexX(s2\c\surf,s2\iv\vert+o)
		pY=VertexY(s2\c\surf,s2\iv\vert+o)	
		If (x2-x1)*(pY-y2)-(pX-x2)*(y2-y1)>0
			If (x4-x2)*(pY-y4)-(pX-x4)*(y4-y2)>0
				If (x3-x4)*(pY-y3)-(pX-x3)*(y3-y4)>0
					If (x1-x3)*(pY-y1)-(pX-x1)*(y1-y3)>0
						Return True
					EndIf
				EndIf
			EndIf
		EndIf
	Next
End Function

; sets a sprites texture
Function SpriteTexture(s.smSprite,texture%)
	FreeTexture s\c\tex
	s\c\tex=texture
	EntityTexture s\c\mesh,texture
End Function

; returns the texture associated with the sprite
Function GetSpriteTexture(s.smSprite)
	Return s\c\tex
End Function

; returns the mesh associated with the sprite
Function GetSpriteMesh(s.smSprite)
	Return s\c\mesh
End Function

; find image in container (internal function)
Function sm_findimage(ref$,c.smContainer)
	Local i%,flag%=False
	If Int(ref$)>0
		i=Int(ref$)-1
		If (i>=0 And i<=c\numimages-1) flag=True
	Else
		For d=0 To c\numimages-1
			If Lower$(c\def[d]\name$)=Lower$(ref$) Then i=d : flag=True : Exit
		Next
	EndIf
	If flag=False i=-1
	Return i
End Function

; return filename without extension from a string (internal function)
Function sm_filename$(txt$)
	Local filename$=txt$ , l%
	For d=Len(txt$) To 1 Step -1
		If Mid$(txt$,d,1)="\"
			filename$=Right$(txt$,Len(txt$)-d)
			Exit
		EndIf
	Next
	l=Len(filename$)
	If Mid$(filename$,l-3,1)="." filename$=Left$(filename$,l-4)
	filename$=Replace$(filename$," ","_")
	Return filename$
End Function