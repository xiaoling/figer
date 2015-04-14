url0='https://drive.google.com/uc?export=download&id=0B52yRXcdpG6MWlVXaTFXWVZQYjg'
url1='https://drive.google.com/uc?export=download&id=0B52yRXcdpG6Mbm1TdHhYdVBmSnM'
if [ "$1" = "new" ]
then 
  file=figer.new.model.gz
  url=$url1
  echo "Getting the new model..."
else
  file=figer.model.gz
  url=$url0
  echo "Getting the model..."
fi

wget --load-cookie cookie.txt --save-cookie cookie.txt "${url}" -O tmp
c=`grep -o "confirm=...." tmp`
wget --load-cookie cookie.txt --save-cookie cookie.txt "${url}&$c" -O $file
rm cookie.txt tmp
