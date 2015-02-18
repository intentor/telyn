; "ImageMaster.bb" - include

Type ImagePack
	Field numimages%                      ; number of images in pack
	Field image                           ; main image containing all packed images
	Field name$[255]                      ; name of packed image
	Field x%[255],y%[255],w%[255],h%[255] ; location of packed image in main image
	Field framesH%[255],framesV%[255]     ; number of frames horizontally and vertically
End Type

; load image pack
Function LoadPack(filename$)
	If FileType(filename$)<>1 RuntimeError "LoadPack()"+Chr$(10)+"pack does not exist:"+Chr$(10)+filename$
	Local deffile$=Left$(filename$,Len(filename$)-4)+".def"
	If FileType(deffile$)<>1 RuntimeError "LoadPack()"+Chr$(10)+"*.def Not found:"+Chr$(10)+deffile$
	Local img=LoadImage(filename$)
	If img=0 RuntimeError "LoadPack()"+Chr$(10)+"failed to load image:"+Chr$(10)+filename$
	Local file=ReadFile(deffile$)
	Local	pack.ImagePack=New ImagePack
	pack\image=img
	Local i%=0
	While Not Eof(file)
		If ReadLine$(file)="{"
			pack\name$[i]=Replace$(ReadLine$(file),Chr$(9),"")
			pack\x[i]=Int(ReadLine$(file)) :pack\y[i]=Int(ReadLine$(file))
			pack\w[i]=Int(ReadLine$(file)) : pack\h[i]=Int(ReadLine$(file))
			pack\framesH[i]=Int(ReadLine$(file)) : pack\framesV[i]=Int(ReadLine$(file))
			Local border$=ReadLine$(file)
			If border$<>"}"
				Local b=Int(ReadLine$(file))
				pack\x[i]=pack\x[i]+b
				pack\y[i]=pack\y[i]+b
				pack\w[i]=pack\w[i]-b*2
				pack\h[i]=pack\h[i]-b*2
			EndIf
			i=i+1
		EndIf
	Wend
	CloseFile file
	pack\numimages=i
	Return Handle(pack)
End Function

; free the pack (and main image)
Function FreePack(packIndex%)
	Local pack.ImagePack=Object.ImagePack(packIndex)
	FreeImage pack\image
	Delete pack
End Function

; return number of images in pack
Function CountPackedImages(packIndex%)
	Local pack.ImagePack=Object.ImagePack(packIndex)
	If pack=Null RuntimeError "CountPackedImages()"+Chr$(10)+"pack does not exist"
	Return pack\numimages
End Function

; grab image from pack (creates a new image)
Function GetPackedImage(ref$,packIndex%)
	Local pack.ImagePack=Object.ImagePack(packIndex)
	Local i%=im_findimage(ref$,pack)
	If i=-1 RuntimeError "GetPackedImage() - image not in pack:"+Chr$(10)+ref$
	Local img%
	; animated ?
	If (pack\framesH[i]*pack\framesV[i])>2
		Local x%,y%,frame%=0
		Local numframes%=pack\framesH[i]*pack\framesV[i]
		Local fw%=pack\w[i]/pack\framesH[i] , fh%=pack\h[i]/pack\framesV[i]
		img=CreateImage(fw,fh,numframes)
		For y=0 To pack\framesV[i]-1
			For x=0 To pack\framesH[i]-1
				CopyRect pack\x[i]+(x*fw),pack\y[i]+(y*fh),fw,fh , 0,0 , ImageBuffer(pack\image),ImageBuffer(img,frame)
				frame=frame+1
			Next
		Next
	Else
		img=CreateImage(pack\w[i],pack\h[i])
		CopyRect pack\x[i],pack\y[i],pack\w[i],pack\h[i] , 0,0 , ImageBuffer(pack\image),ImageBuffer(img)
	EndIf
	Return img
End Function

; return name of image in pack (via reference number)
Function PackedImageName$(index%,packIndex%)
	Local pack.ImagePack=Object.ImagePack(packIndex)
	Return pack\name$[index]
End Function

; return number of animation frames in a packed image
Function PackedImageFrames(ref$,packIndex%)
	Local pack.ImagePack=Object.ImagePack(packIndex)
	Local i%=im_findimage(ref$,pack)
	If i=-1 RuntimeError "PackedImageFrames() - image not in pack:" +Chr$(10)+ref$
	Local numframes=(pack\framesH[i]*pack\framesV[i])
	If numframes=2 numframes=1
	Return numframes
End Function

; find image in pack (internal function)
Function im_findimage(ref$,pack.ImagePack)
	Local i%,d%,flag%=False
	If Int(ref$)>0
		i=Int(ref$)-1
		If (i>=0 And i<=pack\numimages-1) flag=True
	Else
		For d=0 To pack\numimages-1
			If Lower$(pack\name$[d])=Lower$(ref$) Then i=d : flag=True : Exit
		Next
	EndIf
	If flag=False i=-1
	Return i
End Function