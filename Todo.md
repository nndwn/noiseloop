## Todo
- ~~Desain logo icon app~~
- ~~Splash App~~
- ~~Buat UI top bar dengan warna dinamic menyamain warna ui status bar~~
- ~~Buat Ui item Daftar Music lalu terapkan daftar music pada main ui~~
- ~~buat animation wave jika music dimainkan~~
- ~~pada scafold tambahkan bottom ui untuk menampilkan musik yang dimainkan~~
- ~~hubungkan viewmodel dengan ui (lakukan simulasi)~~
- ~~buat music berjalan ke background tanpa jeda~~
- ~~buat notifikasi bar ketika musik diputar~~
- ~~hubungkan dengan google ads manfaatkan datastore iklan muncul berdasarkan berapa lama user memakainya~~
- ~~ui peringtan untuk tampil ads ketika user memainkan sound~~
- ~~tambah suara hingga 10 list atau lebih kalau bisa ( dan 10nya lagi playing secara online)~~
- ~~manambahakan audio data label online dan offline, jika data sudah di download atau di terima buat jadi offline~~
- ~~buat cover music dengan ai dengan kata kunci `buatkan saya sebuah gambar abstrak random color dominasi color #581989 mirip seperti rain-on-umbrella dengan ratio 1:1`~~
- ~~buat shimmer pada saat list awal di buka~~
- ~~setelah shimmer selesai langsung upload bagaimana cara nya release app store ? mengunakan android studio~~
- ~~penambahan detail music UI~~
- ~~hubungkan ke google billin~~
- ~~hubungkan ke google review~~
- ~~tambhak feedback issue~~
- ~~pada ui list buat semacam slide dapat di geser menjadi favorite~~ 
- ~~buat untuk UI tablet~~

## Issue
- ~~splsh di api 31 kebawah tampak terlalu besar density 440 dpi~~
- ~~entah kenapa status bar berwarna hitam di setiap device~~
- ~~background play perlu shadow~~
- ~~bottom bar tertutupin dengan button bar bawaan hape~~
- ~~pada segment bottom bar memiliki issue warna di latar belakang mengikutin warna container~~
    - masalah nya list item membutuhkan space di bawahnya maka nya seakan seakan memiliki background karena di awal langsung di tentukan tinggi nya sama innervalue
- pada rain Blue terdapat jeda audio kecil tapi menganggu 
- ~~ketika rain blue di hapus di singleton user yang telah install masih ada.~~
- ~~pada top diperbaikin kali tidak menjorok keatas mengenai status bar~~
- ~~font tag filter sedikit perkecil lagi mungkin kurangin 1 sp~~
- ~~tambahkan icon download apakah sudah di download apa belum~~
- ~~mungkin perlu tambahan notif ketika user menambahkan favorite atau tidak~~
- ~~tambahkan notice snackbar setelah user melakukan purchase~~
- ~~tambahkan snackbar lagi sebelum sudah ada snackbar di miniplay tapi miniplay tidak muncul saat user belum memainkan audio sama sekali , bagaima nanti nya jika user lakukan secara offline lalu memainkan musik yang online?~~
- ~~jika user sudah purchase tidak perlu lagi tombol purchase di sidebar~~
- ~~ubah settingan agp sebelum ini masaih settingan agp 8 migrasikan ke agp 9~~
- di dalam child ada scafold yang tidak di butuhkan seharusnya
- the storm pass audio nya memiliki jeda
- perbaikin struktur kode terapkan event, state, effect
- struktur UI bisa di optimize lagi perlu di text di virtual device di lingkungan sdk 35 keatas


## Create Java Home for command
- Environment Variable > System variables > New
- Java_Home
- C:\Users\xenial\AppData\Local\Programs\Android Studio\jbr
- Environment Variable > System variables > Path > New
- %JAVA_HOME%\bin


```
gradlew clean bundleRelease
```
```
gradlew assembleRelease
```