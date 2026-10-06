package com.example.songchords.repository

import com.example.songchords.auth.UserIdentityManager
import com.example.songchords.model.Song

object SampleSongs {

    val TU_POETA = Song(
        id = "sample_tu_poeta",
        title = "Tu Poeta",
        artist = "Alex Campos",
        originalKey = "G",
        tempo = 72,
        timeSignature = "4/4",
        tags = listOf("Alabanza", "Adoración", "Worship", "Praise", "Spanish"),
        createdByUserId = UserIdentityManager.SUPER_ADMIN_ID,
        createdByName = UserIdentityManager.SUPER_ADMIN_NAME,
        content = """
            [Intro]
            [G]  [Cadd2]  [D]  [Em]

            [Verse 1]
            [G] Cuando desperté allí estabas tú[D]
            Aquella mujer con la que soñé[Em]
            Le vestí la piel sus labios bes[Cadd2]é
            Y en su corazón allí me refugié[G]

            [Verse 2]
            Late el corazón, late hoy por vos[D]
            Y si canto yo, canto para ti[Em]
            Mi vida entera toda te la doy[Cadd2]
            Ya no tengas miedo yo aquí esto[G]y [D]

            [Chorus]
            Tu poet[G]a, tu verano[D]
            El silenc[Am]io de mi v[Em]oz diciendo te am[D]o
            Mi prince[G]sa, mi primaver[D]a
            Mi ternu[C]ra y mi am[G]or por vez primer[D]a  
            
            [Verse 3]
            Me c[G]onoces bien siempre te amar[D]é
            Aunque estemos lejos yo aquí estaré[Em]
            Seré tu poema tu razón de ser[Cadd2]
            Tú serás mi reina, mi única mujer[G]
           
            [Verse 4]
            Tú serás mi aliento serás mi edén[D]
            Mi eterno romance confidente fiel[Em]
            Niña consentida amor de mi ser[Cadd2]
            Agua de este río que yo beber[G]é [D]
            
            [Chorus]
            
            [Outro]
            [F]  [C]  [G]  [Em]  [F]  [C]  [G]  [E]
            
            [Chorus]
            Tu poet[A]a, tu verano[E]
            El silenc[Bm]io de mi v[F#m]oz diciendo te am[E]o
            Mi prince[A]sa, mi primaver[E]a
            Mi ternu[D]ra y mi am[A]or por vez primer[E]a  
            Soy un niño[A] enamorado[E]
            El dise[D]ño de tu sue[A]ño en mi plant[E]ado
            Consentid[A]a mariposita[E]
            Prision[D]era de este amo[A]r que no se rind[E]e
            
        """.trimIndent()
    )

    val RENUEVAME = Song(
        id = "sample_renuevame",
        title = "Renuévame",
        artist = "Marcos Witt",
        originalKey = "D",
        tempo = 68,
        timeSignature = "4/4",
        tags = listOf("Adoración", "Worship", "Alabanza", "Praise"),
        createdByUserId = UserIdentityManager.SUPER_ADMIN_ID,
        createdByName = UserIdentityManager.SUPER_ADMIN_NAME,
        content = """
            [Intro]
            [D]  [G]  [A]  [D]

            [Verse 1]
            [D]Renuéva[G]me, Se[A]ñor Jes[Bm]ús
            [G]Ya no quie[Em]ro ser igual[A]
            [D]Renuéva[G]me, Se[A]ñor Jes[Bm]ús
            [G]Pon en mí tu c[Em]orazón[A]

            [Chorus]
            Porque [D]todo lo que hay [A/C#]dentro de [Bm]mí[A]
            [G]Necesita ser cambi[Em]ado, Se[A]ñor
            Porque [D]todo lo que hay [A/C#]dentro de mi cor[Bm]azón[A]
            [G]Necesita [A]más de [D]ti

            [Outro]
            [G]Necesita [A]más de [D]ti
        """.trimIndent()
    )

    val LA_BONDAD_DE_DIOS = Song(
        id = "sample_la_bondad_de_dios",
        title = "La Bondad de Dios",
        artist = "Bethel Music / Miel San Marcos",
        originalKey = "G",
        tempo = 70,
        timeSignature = "4/4",
        tags = listOf("Adoración", "Worship", "Alabanza", "Praise"),
        createdByUserId = UserIdentityManager.SUPER_ADMIN_ID,
        createdByName = UserIdentityManager.SUPER_ADMIN_NAME,
        content = """
            [Intro]
            [G]  [C]  [G]  [C]

            [Verse 1]
            Te [G]amo Dios, tu am[C]or nunca me fa[G]lla
            M[D]i existi[E]r en tus m[C]anos es[D]ta
            Desde el momento en que me des[Em]pierto [C]
            Hasta el an[G]oche[D/F#]r [Em]
            Yo can[C]taré de la bon[D]dad de [G]Dios

            [Chorus]
            E[C]n mi vida has sido b[G]ueno
            E[C]n mi vida has sido t[G]an, tan fi[D]el
            C[C]on mi ser, con cada al[G]iento [D/F#] [Em]
            Yo canta[C]ré de la b[D]ondad de D[G]ios

            [Verse 2]
            Y[G]o amo tu vo[C]z mantiene tu voz 
            Me has gu[C]iado por el fu[G]ego 
            Tu cerca es[D/F#]tás [Em]
            En l[C]a oscurid[D]ad
            Te conozco como un p[Em]adre [C]
            Y como am[G]igo [D/F#]fiel [Em]
            Mi vida est[C]á en la bon[D]dad de [G]Dios

            [Chorus]
            
            [Bridge]
            T[G/B]u fidelidad s[C]igue
            [D]Persiguiendom[G]e
            T[G/B]u fidelidad s[C]igue
            [D]Persiguiendom[G]e
            Todo lo que [G/B]soy 
            Te lo entrego h[G]oy
            A [D]ti me rendir[Em]é
            T[G/B]u fidelidad s[C]igue
            [D]Persiguiendom[G]e (Bis)
            
            [Chorus]
            
        """.trimIndent()
    )

    val CUAN_GRANDE_ES_EL = Song(
        id = "sample_cuan_grande_es_el",
        title = "Cuan Grande Es Él",
        artist = "Tradicional / Himno",
        originalKey = "A",
        tempo = 74,
        timeSignature = "4/4",
        tags = listOf("Himno", "Adoración", "Worship", "Praise"),
        createdByUserId = UserIdentityManager.SUPER_ADMIN_ID,
        createdByName = UserIdentityManager.SUPER_ADMIN_NAME,
        content = """
            [Intro]
            [A]  [D]  [A]  [E]  [A]

            [Verse 1]
            Se[A]ñor mi Dios, al contemplar los ci[D]elos
            El firmam[A]ento y las es[E]trellas mi[A]l
            Al oír tu vo[A]z en los potentes tr[D]uenos
            Y al ver brill[A]ar el s[E]ol en su ceni[A]t

            [Chorus]
            Mi coraz[A]ón ento[D]na la canci[A]ón
            ¡Cuán grande es [E]Él! ¡Cuán grande es [E]Él!
            Mi coraz[A]ón ento[D]na la canci[A]ón
            ¡Cuán grande es [E]Él! ¡Cu[F#m]án gr[Bm]ande es [A]Él!

            [Verse 2]
            Al re[A]correr los montes y los va[D]lles
            Y ver las be[A]llas flo[E]res al pas[A]ar
            Al escu[A]char el canto de las a[D]ves
            Y el mu[A]rmurar del cla[E]ro manant[A]ial

            [Verse 3]
            Cu[A]ndo recuerdo del amor div[A]ino
            Que desde el cie[A]lo al sa[E]lvador enví[A]o
            Aque[A]l Jesús que por salvarme vi[D]no
            Y en una cr[A]uz sufri[E]ó y por mi muri[A]ó
            
            [Verse 4]
            Cuando el seño[A]r me llame a su prese[D]ncia
            Al dulce hoga[A]r, al cie[E]lo de esplendo[A]r
            Le adorar[A]é cantando su grande[D]za
            De su pod[A]er y s[E]u infinto am[A]or
            
        """.trimIndent()
    )

    val WAY_MAKER = Song(
        id = "sample_way_maker",
        title = "Way Maker (Es El Que Abre Caminos)",
        artist = "Sinach / Way Maker Spanish",
        originalKey = "E",
        tempo = 68,
        timeSignature = "4/4",
        tags = listOf("Adoración", "Worship", "Alabanza", "Praise"),
        createdByUserId = UserIdentityManager.SUPER_ADMIN_ID,
        createdByName = UserIdentityManager.SUPER_ADMIN_NAME,
        content = """
            [Intro]
            [E]  [B]  [F#m]  [A]

            [Verse 1]
            [E]Aquí estás, te vemos mo[B]ver
            Te adora[F#m]ré, te adora[A]ré
            [E]Aquí estás, obrando en mí[B]
            Te adora[F#m]ré, te adora[A]ré

            [Chorus]
            [E]Milagroso, abres camino, cumples prome[B]sas
            Luz en tinieblas, mi Dios, así eres [F#m]tú
            Milagroso, abres camino, cumples prome[A]sas
            Luz en tinieblas, mi Dios, así eres tú
        """.trimIndent()
    )

    val SUBLIME_GRACIA = Song(
        id = "sample_sublime_gracia",
        title = "Sublime Gracia (Amazing Grace)",
        artist = "Tradicional / Chris Tomlin",
        originalKey = "G",
        tempo = 70,
        timeSignature = "3/4",
        tags = listOf("Himno", "Adoración", "Worship", "Praise"),
        createdByUserId = UserIdentityManager.SUPER_ADMIN_ID,
        createdByName = UserIdentityManager.SUPER_ADMIN_NAME,
        content = """
            [Intro]
            [G]  [C/G]  [G]  [D/G]

            [Verse 1]
            Su[G]blime gracia de[C]l Se[G]ñor
            Que a un [G]infeliz salv[D]ó
            Fui ci[G]ego mas hoy [C]miro [G]yo
            Perd[Em]ido y [D]él me ha[G]lló

            [Verse 2]
            Su [G]gracia me ense[C]ñó a te[G]mer
            Mis [G]dudas ahuyen[D]tó
            ¡Oh [G]cuán preciosa [C]fue a mi se[G]r
            Cuando [Em]él me tra[D]nsform[G]ó

            [Verse 3]
            En l[G]os peligros y a[C]flicció[G]n
            Que yo he tenido aqu[D]í
            Su gra[G]cia siempre m[C]e libr[G]ó
            Y m[Em]e guiar[D]á fel[G]iz

            [Verse 4]
            Y cu[G]ando sion por s[C]iglos m[G]il
            Brillando esté cual s[D]ol
            Yo ca[G]ntaré por si[C]empre all[G]í
            Su am[Em]or que m[D]e salv[G]ó
            
        """.trimIndent()
    )

    val ALL_SONGS = listOf(
        TU_POETA,
        RENUEVAME,
        LA_BONDAD_DE_DIOS,
        CUAN_GRANDE_ES_EL,
        WAY_MAKER,
        SUBLIME_GRACIA
    )
}
