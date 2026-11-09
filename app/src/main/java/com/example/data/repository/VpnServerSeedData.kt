package com.example.data.repository

import com.example.data.local.entity.ServerEntity

object VpnServerSeedData {

    fun getGlobalServers(): List<ServerEntity> = listOf(
        // === A ===
        ServerEntity("srv_af_kbl_01", "AF", "Afghanistan", "🇦🇫", "Kabul", "kbl1.protonplusvpn.net", "111.119.172.10", 51820, "aFg11HaNiStAn22KbL33WxYz0123456789AbCdEfGh=", 36, 21, 0.0f, 10000, false, false, 34.55f, 69.20f),
        ServerEntity("srv_al_tia_01", "AL", "Albania", "🇦🇱", "Tirana", "tia1.protonplusvpn.net", "185.120.55.10", 51820, "aLb44NiAtIa11PqRsTuVwXyZaBcDeFgHiJkLmNoPqRs=", 22, 18, 0.0f, 10000, true, false, 41.33f, 19.82f),
        ServerEntity("srv_dz_alg_01", "DZ", "Algeria", "🇩🇿", "Algiers", "alg1.protonplusvpn.net", "185.120.56.10", 51820, "aLg11ErIaAlG22WxYz0123456789AbCdEfGhIjKl=", 24, 20, 0.0f, 10000, false, false, 36.75f, 3.06f),
        ServerEntity("srv_ad_alv_01", "AD", "Andorra", "🇦🇩", "Andorra la Vella", "alv1.protonplusvpn.net", "185.120.55.15", 51820, "aNd77OrRaLv22WxYz0123456789AbCdEfGhIjKlMnOp=", 19, 14, 0.0f, 10000, false, false, 42.51f, 1.52f),
        ServerEntity("srv_ar_eze_01", "AR", "Argentina", "🇦🇷", "Buenos Aires", "eze1.protonplusvpn.net", "199.180.30.10", 51820, "aRg77EnTiNaE88XyZaBcDeFgHiJkLmNoPqRsTuVw=", 34, 22, 0.0f, 10000, false, false, -34.60f, -58.38f),
        ServerEntity("srv_am_evn_01", "AM", "Armenia", "🇦🇲", "Yerevan", "evn1.protonplusvpn.net", "95.173.150.10", 51820, "aRm22EnIaEvN33GhIjKlMnOpQrStUvWxYz0123456=", 32, 19, 0.0f, 10000, false, false, 40.18f, 44.50f),
        ServerEntity("srv_au_syd_01", "AU", "Australia", "🇦🇺", "Sydney", "syd1.protonplusvpn.net", "139.130.4.5", 51820, "aUs66TrAlIaS77DeFgHiJkLmNoPqRsTuVwXyZaBc=", 29, 22, 0.0f, 20000, true, false, -33.87f, 151.21f),
        ServerEntity("srv_au_mel_01", "AU", "Australia", "🇦🇺", "Melbourne", "mel1.protonplusvpn.net", "139.130.4.15", 51820, "aUs88MeLbOuRnE11DeFgHiJkLmNoPqRsTuVwXyZaBc=", 30, 20, 0.0f, 20000, false, false, -37.81f, 144.96f),
        ServerEntity("srv_au_per_01", "AU", "Australia", "🇦🇺", "Perth", "per1.protonplusvpn.net", "139.130.4.25", 51820, "aUs99PeRtHwA22DeFgHiJkLmNoPqRsTuVwXyZaBcDe=", 32, 18, 0.0f, 10000, false, false, -31.95f, 115.86f),
        ServerEntity("srv_at_vie_01", "AT", "Austria", "🇦🇹", "Vienna", "vie1.protonplusvpn.net", "185.120.45.10", 51820, "aUs88TrIaViE33MnOpQrStUvWxYz0123456789AbCd=", 14, 21, 0.0f, 20000, true, false, 48.21f, 16.37f),
        ServerEntity("srv_az_gyd_01", "AZ", "Azerbaijan", "🇦🇿", "Baku", "gyd1.protonplusvpn.net", "95.173.150.20", 51820, "aZe33ErBaIjaN44IjKlMnOpQrStUvWxYz01234567=", 30, 21, 0.0f, 10000, false, false, 40.41f, 49.87f),

        // === B ===
        ServerEntity("srv_bh_bah_01", "BH", "Bahrain", "🇧🇭", "Manama", "bah1.protonplusvpn.net", "185.120.52.80", 51820, "bAh22RaInMaN33StUvWxYz0123456789AbCdEfGhIj=", 25, 22, 0.0f, 20000, true, false, 26.22f, 50.58f),
        ServerEntity("srv_bd_dac_01", "BD", "Bangladesh", "🇧🇩", "Dhaka", "dac1.protonplusvpn.net", "111.119.170.10", 51820, "bAn88GlAdEsH99StUvWxYz0123456789AbCdEfGh=", 35, 30, 0.0f, 10000, false, false, 23.81f, 90.41f),
        ServerEntity("srv_be_bru_01", "BE", "Belgium", "🇧🇪", "Brussels", "bru1.protonplusvpn.net", "185.120.45.20", 51820, "bEl99GiUmBrU44DeFgHiJkLmNoPqRsTuVwXyZaBcDe=", 13, 25, 0.0f, 20000, true, false, 50.85f, 4.35f),
        ServerEntity("srv_bo_lpz_01", "BO", "Bolivia", "🇧🇴", "La Paz", "lpz1.protonplusvpn.net", "199.180.30.35", 51820, "bOl33IvIaLaP44ZaBcDeFgHiJkLmNoPqRsTuVwXyZa=", 35, 17, 0.0f, 10000, false, false, -16.50f, -68.11f),
        ServerEntity("srv_ba_sjj_01", "BA", "Bosnia and Herzegovina", "🇧🇦", "Sarajevo", "sjj1.protonplusvpn.net", "185.120.55.65", 51820, "bOs44NiAsArA55JkLmNoPqRsTuVwXyZaBcDeFgHiJk=", 23, 19, 0.0f, 10000, false, false, 43.85f, 18.41f),
        ServerEntity("srv_br_gru_01", "BR", "Brazil", "🇧🇷", "São Paulo", "gru1.protonplusvpn.net", "199.180.30.20", 51820, "bRa88ZiLgRu99ZaBcDeFgHiJkLmNoPqRsTuVwXyZ=", 28, 28, 0.0f, 20000, true, false, -23.55f, -46.63f),
        ServerEntity("srv_br_gig_01", "BR", "Brazil", "🇧🇷", "Rio de Janeiro", "gig1.protonplusvpn.net", "199.180.30.25", 51820, "bRa77RiOdEjA88ZaBcDeFgHiJkLmNoPqRsTuVwXyZ=", 29, 24, 0.0f, 20000, false, false, -22.90f, -43.17f),
        ServerEntity("srv_bg_sof_01", "BG", "Bulgaria", "🇧🇬", "Sofia", "sof1.protonplusvpn.net", "185.120.55.25", 51820, "bUl11GaRiASoF55ZaBcDeFgHiJkLmNoPqRsTuVwXyZ=", 24, 20, 0.0f, 10000, false, false, 42.70f, 23.32f),

        // === C ===
        ServerEntity("srv_kh_pnh_01", "KH", "Cambodia", "🇰🇭", "Phnom Penh", "pnh1.protonplusvpn.net", "103.253.145.20", 51820, "cAm44BoDiApN55DeFgHiJkLmNoPqRsTuVwXyZaBcDe=", 26, 22, 0.0f, 10000, false, false, 11.55f, 104.92f),
        ServerEntity("srv_ca_yyz_01", "CA", "Canada", "🇨🇦", "Toronto", "yyz1.protonplusvpn.net", "199.180.25.10", 51820, "cAn99AdAyYz00BcDeFgHiJkLmNoPqRsTuVwXyZaB=", 15, 24, 0.0f, 30000, true, false, 43.65f, -79.38f),
        ServerEntity("srv_ca_yul_01", "CA", "Canada", "🇨🇦", "Montreal", "yul1.protonplusvpn.net", "199.180.25.30", 51820, "cAn77MoNtReA88CdEfGhIjKlMnOpQrStUvWxYz0123=", 16, 22, 0.0f, 20000, false, false, 45.50f, -73.56f),
        ServerEntity("srv_ca_yvr_01", "CA", "Canada", "🇨🇦", "Vancouver", "yvr1.protonplusvpn.net", "199.180.25.20", 51820, "cAn88VaNcOuV99BcDeFgHiJkLmNoPqRsTuVwXyZaBc=", 18, 20, 0.0f, 20000, true, false, 49.28f, -123.12f),
        ServerEntity("srv_cl_scl_01", "CL", "Chile", "🇨🇱", "Santiago", "scl1.protonplusvpn.net", "199.180.30.30", 51820, "cHi11LeScL22DeFgHiJkLmNoPqRsTuVwXyZaBcDe=", 33, 20, 0.0f, 10000, false, false, -33.45f, -70.67f),
        ServerEntity("srv_cn_pek_01", "CN", "China", "🇨🇳", "Beijing", "pek1.protonplusvpn.net", "103.253.141.10", 51820, "cHi55NaBeIjI66KlMnOpQrStUvWxYz0123456789Ab=", 28, 25, 0.0f, 20000, true, false, 39.90f, 116.40f),
        ServerEntity("srv_cn_sha_01", "CN", "China", "🇨🇳", "Shanghai", "sha1.protonplusvpn.net", "103.253.141.20", 51820, "cHi66NaShAnG77LmNoPqRsTuVwXyZaBcDeFgHiJkLm=", 27, 26, 0.0f, 20000, true, false, 31.23f, 121.47f),
        ServerEntity("srv_co_bog_01", "CO", "Colombia", "🇨🇴", "Bogotá", "bog1.protonplusvpn.net", "199.180.30.40", 51820, "cOl22OmBiAbO33FgHiJkLmNoPqRsTuVwXyZaBcDe=", 31, 23, 0.0f, 10000, false, false, 4.71f, -74.07f),
        ServerEntity("srv_cr_sjo_01", "CR", "Costa Rica", "🇨🇷", "San José", "sjo1.protonplusvpn.net", "199.180.30.50", 51820, "cOs33TaRiCaS44HiJkLmNoPqRsTuVwXyZaBcDeFg=", 29, 18, 0.0f, 10000, false, false, 9.93f, -84.09f),
        ServerEntity("srv_hr_zag_01", "HR", "Croatia", "🇭🇷", "Zagreb", "zag1.protonplusvpn.net", "185.120.55.30", 51820, "cRo22AtIaZaG66HiJkLmNoPqRsTuVwXyZaBcDeFgHi=", 21, 19, 0.0f, 10000, false, false, 45.81f, 15.98f),
        ServerEntity("srv_cy_nic_01", "CY", "Cyprus", "🇨🇾", "Nicosia", "nic1.protonplusvpn.net", "185.120.55.35", 51820, "cYp33RuSNiC77LmNoPqRsTuVwXyZaBcDeFgHiJkLm=", 27, 22, 0.0f, 10000, false, false, 35.19f, 33.38f),
        ServerEntity("srv_cz_prg_01", "CZ", "Czech Republic", "🇨🇿", "Prague", "prg1.protonplusvpn.net", "185.120.45.30", 51820, "cZe44ChPrG88NoPqRsTuVwXyZaBcDeFgHiJkLmNoP=", 15, 23, 0.0f, 20000, true, false, 50.08f, 14.44f),

        // === D ===
        ServerEntity("srv_dk_cph_01", "DK", "Denmark", "🇩🇰", "Copenhagen", "cph1.protonplusvpn.net", "185.120.45.40", 51820, "dEn55MaRkCpH99QrStUvWxYz0123456789AbCdEfGh=", 16, 20, 0.0f, 20000, true, false, 55.68f, 12.57f),

        // === E ===
        ServerEntity("srv_ec_uio_01", "EC", "Ecuador", "🇪🇨", "Quito", "uio1.protonplusvpn.net", "199.180.30.60", 51820, "eCu44AdOrUiO55JkLmNoPqRsTuVwXyZaBcDeFgHi=", 33, 19, 0.0f, 10000, false, false, -0.18f, -78.47f),
        ServerEntity("srv_eg_cai_01", "EG", "Egypt", "🇪🇬", "Cairo", "cai1.protonplusvpn.net", "185.120.56.20", 51820, "eGy22PtCaIrO33ZaBcDeFgHiJkLmNoPqRsTuVwXy=", 28, 29, 0.0f, 10000, true, false, 30.04f, 31.24f),
        ServerEntity("srv_eg_aly_01", "EG", "Egypt", "🇪🇬", "Alexandria", "aly1.protonplusvpn.net", "185.120.56.25", 51820, "eGy44PtAlExA55ZaBcDeFgHiJkLmNoPqRsTuVwXyZa=", 29, 23, 0.0f, 10000, false, false, 31.20f, 29.91f),
        ServerEntity("srv_ee_tll_01", "EE", "Estonia", "🇪🇪", "Tallinn", "tll1.protonplusvpn.net", "185.120.45.50", 51820, "eSt66OnIaTlL00TuVwXyZaBcDeFgHiJkLmNoPqRsTu=", 18, 17, 0.0f, 10000, false, false, 59.44f, 24.75f),
        ServerEntity("srv_et_add_01", "ET", "Ethiopia", "🇪🇹", "Addis Ababa", "add1.protonplusvpn.net", "185.120.56.90", 51820, "eTh77IoPiAaD88BcDeFgHiJkLmNoPqRsTuVwXyZaBc=", 38, 19, 0.0f, 10000, false, false, 9.03f, 38.74f),

        // === F ===
        ServerEntity("srv_fi_hel_01", "FI", "Finland", "🇫🇮", "Helsinki", "hel1.protonplusvpn.net", "185.120.45.60", 51820, "fIn77LaNdHeL11UvWxYz0123456789AbCdEfGhIjKl=", 17, 19, 0.0f, 20000, true, false, 60.17f, 24.94f),
        ServerEntity("srv_fr_par_01", "FR", "France", "🇫🇷", "Paris", "par1.protonplusvpn.net", "185.120.46.20", 51820, "fRa88NcEpAr22WxYz0123456789AbCdEfGhIjKlMn=", 12, 28, 0.0f, 20000, true, false, 48.86f, 2.35f),
        ServerEntity("srv_fr_mrs_01", "FR", "France", "🇫🇷", "Marseille", "mrs1.protonplusvpn.net", "185.120.46.30", 51820, "fRa66MaRsEiL77WxYz0123456789AbCdEfGhIjKlMn=", 14, 21, 0.0f, 20000, false, false, 43.30f, 5.37f),

        // === G ===
        ServerEntity("srv_ge_tbs_01", "GE", "Georgia", "🇬🇪", "Tbilisi", "tbs1.protonplusvpn.net", "95.173.150.30", 51820, "gEo44RgIaTbS55KlMnOpQrStUvWxYz0123456789=", 28, 20, 0.0f, 10000, false, false, 41.72f, 44.83f),
        ServerEntity("srv_de_ber_01", "DE", "Germany", "🇩🇪", "Berlin", "ber1.protonplusvpn.net", "185.120.44.18", 51820, "bEr89ZkL12PqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuV=", 13, 26, 0.0f, 20000, false, false, 52.52f, 13.40f),
        ServerEntity("srv_de_fra_01", "DE", "Germany", "🇩🇪", "Frankfurt", "fra1.protonplusvpn.net", "185.120.44.12", 51820, "x5G0zWbF7Yw0vO4t91RkQmB4Jz9Nl8OpQ2rStUvWxYz=", 11, 22, 0.0f, 40000, true, false, 50.11f, 8.68f),
        ServerEntity("srv_de_muc_01", "DE", "Germany", "🇩🇪", "Munich", "muc1.protonplusvpn.net", "185.120.44.25", 51820, "mUc33MuNiChG44PqRsTuVwXyZaBcDeFgHiJkLmNoPq=", 12, 20, 0.0f, 20000, false, false, 48.14f, 11.58f),
        ServerEntity("srv_gh_acc_01", "GH", "Ghana", "🇬🇭", "Accra", "acc1.protonplusvpn.net", "185.120.56.70", 51820, "gHa55NaAcCrA66BcDeFgHiJkLmNoPqRsTuVwXyZaBc=", 36, 18, 0.0f, 10000, false, false, 5.60f, -0.18f),
        ServerEntity("srv_gr_ath_01", "GR", "Greece", "🇬🇷", "Athens", "ath1.protonplusvpn.net", "185.120.55.40", 51820, "gRe99EcEaTh33ZaBcDeFgHiJkLmNoPqRsTuVwXyZa=", 26, 21, 0.0f, 10000, false, false, 37.98f, 23.73f),

        // === H ===
        ServerEntity("srv_hk_hkg_01", "HK", "Hong Kong", "🇭🇰", "Hong Kong", "hkg1.protonplusvpn.net", "103.253.140.10", 51820, "hOn77gKoNgHk88FgHiJkLmNoPqRsTuVwXyZaBcDe=", 14, 26, 0.0f, 30000, true, false, 22.32f, 114.17f),
        ServerEntity("srv_hu_bud_01", "HU", "Hungary", "🇭🇺", "Budapest", "bud1.protonplusvpn.net", "185.120.45.70", 51820, "hUn11GaRyBuD44AbCdEfGhIjKlMnOpQrStUvWxYz01=", 18, 22, 0.0f, 10000, false, false, 47.50f, 19.04f),

        // === I ===
        ServerEntity("srv_is_kef_01", "IS", "Iceland", "🇮🇸", "Reykjavik", "kef1.protonplusvpn.net", "185.120.45.15", 51820, "iCe33LaNdReY44KlMnOpQrStUvWxYz0123456789Ab=", 18, 16, 0.0f, 10000, true, false, 64.14f, -21.94f),
        ServerEntity("srv_in_bom_01", "IN", "India", "🇮🇳", "Mumbai", "bom1.protonplusvpn.net", "103.253.146.33", 51820, "bOm77WxYz0123456789AbCdEfGhIjKlMnOpQrStUvWx=", 28, 38, 0.0f, 20000, true, false, 19.08f, 72.88f),
        ServerEntity("srv_in_del_01", "IN", "India", "🇮🇳", "New Delhi", "del1.protonplusvpn.net", "103.253.146.10", 51820, "dEl22DeLhIiN33WxYz0123456789AbCdEfGhIjKlMn=", 26, 35, 0.0f, 20000, true, false, 28.61f, 77.20f),
        ServerEntity("srv_in_blr_01", "IN", "India", "🇮🇳", "Bengaluru", "blr1.protonplusvpn.net", "103.253.146.50", 51820, "bLr44BeNgAlU55WxYz0123456789AbCdEfGhIjKlMn=", 27, 30, 0.0f, 20000, false, false, 12.97f, 77.59f),
        ServerEntity("srv_id_jkt_01", "ID", "Indonesia", "🇮🇩", "Jakarta", "jkt1.protonplusvpn.net", "103.253.148.10", 51820, "iNd88OnEsIaJ99HiJkLmNoPqRsTuVwXyZaBcDeFg=", 25, 27, 0.0f, 20000, false, false, -6.21f, 106.85f),
        ServerEntity("srv_id_dps_01", "ID", "Indonesia", "🇮🇩", "Bali", "dps1.protonplusvpn.net", "103.253.148.20", 51820, "bAl33IdBaLiD44HiJkLmNoPqRsTuVwXyZaBcDeFgHi=", 26, 21, 0.0f, 10000, false, false, -8.40f, 115.18f),
        ServerEntity("srv_ir_thr_01", "IR", "Iran", "🇮🇷", "Tehran", "thr1.protonplusvpn.net", "95.173.152.10", 51820, "iRa22NtEhRaN33ZaBcDeFgHiJkLmNoPqRsTuVwXyZa=", 33, 24, 0.0f, 10000, false, false, 35.68f, 51.38f),
        ServerEntity("srv_iq_bgw_01", "IQ", "Iraq", "🇮🇶", "Baghdad", "bgw1.protonplusvpn.net", "185.120.52.55", 51820, "iRa33QBaGhDa44AbCdEfGhIjKlMnOpQrStUvWxYz01=", 31, 23, 0.0f, 10000, false, false, 33.31f, 44.36f),
        ServerEntity("srv_ie_dub_01", "IE", "Ireland", "🇮🇪", "Dublin", "dub1.protonplusvpn.net", "185.120.48.30", 51820, "iRe22LaNdDuB55CdEfGhIjKlMnOpQrStUvWxYz0123=", 14, 24, 0.0f, 20000, true, false, 53.35f, -6.26f),
        ServerEntity("srv_il_tlv_01", "IL", "Israel", "🇮🇱", "Tel Aviv", "tlv1.protonplusvpn.net", "185.120.52.40", 51820, "iSr11AeLtLv22TuVwXyZaBcDeFgHiJkLmNoPqRsTu=", 24, 25, 0.0f, 20000, true, false, 32.09f, 34.78f),
        ServerEntity("srv_it_mil_01", "IT", "Italy", "🇮🇹", "Milan", "mil1.protonplusvpn.net", "185.120.47.30", 51820, "iTa33LyMiL66EfGhIjKlMnOpQrStUvWxYz01234567=", 13, 27, 0.0f, 20000, true, false, 45.46f, 9.19f),
        ServerEntity("srv_it_rom_01", "IT", "Italy", "🇮🇹", "Rome", "rom1.protonplusvpn.net", "185.120.47.40", 51820, "rOm55ItRoMeA66EfGhIjKlMnOpQrStUvWxYz0123456=", 15, 23, 0.0f, 20000, false, false, 41.90f, 12.49f),

        // === J ===
        ServerEntity("srv_jp_tyo_01", "JP", "Japan", "🇯🇵", "Tokyo", "tyo1.protonplusvpn.net", "133.242.18.90", 51820, "tYo14HiJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRs=", 14, 21, 0.0f, 30000, true, false, 35.68f, 139.65f),
        ServerEntity("srv_jp_osa_01", "JP", "Japan", "🇯🇵", "Osaka", "osa1.protonplusvpn.net", "133.242.18.95", 51820, "oSa22JaPaNoS33HiJkLmNoPqRsTuVwXyZaBcDeFgHi=", 16, 20, 0.0f, 20000, false, false, 34.69f, 135.50f),
        ServerEntity("srv_jo_amm_01", "JO", "Jordan", "🇯🇴", "Amman", "amm1.protonplusvpn.net", "185.120.52.50", 51820, "jOr44DaNaMm55KlMnOpQrStUvWxYz0123456789AbC=", 27, 21, 0.0f, 10000, false, false, 31.95f, 35.92f),

        // === K ===
        ServerEntity("srv_kz_ala_01", "KZ", "Kazakhstan", "🇰🇿", "Almaty", "ala1.protonplusvpn.net", "95.173.150.40", 51820, "kAz55AkHsTaN66MnOpQrStUvWxYz0123456789Ab=", 36, 17, 0.0f, 10000, false, false, 43.22f, 76.85f),
        ServerEntity("srv_ke_nbo_01", "KE", "Kenya", "🇰🇪", "Nairobi", "nbo1.protonplusvpn.net", "185.120.56.60", 51820, "kEn55YaNaIrO66BcDeFgHiJkLmNoPqRsTuVwXyZaBc=", 37, 20, 0.0f, 10000, false, false, -1.29f, 36.82f),
        ServerEntity("srv_kw_kwi_01", "KW", "Kuwait", "🇰🇼", "Kuwait City", "kwi1.protonplusvpn.net", "185.120.52.75", 51820, "kUw22AiTkWi33StUvWxYz0123456789AbCdEfGhIj=", 24, 25, 0.0f, 20000, true, false, 29.37f, 47.97f),

        // === L ===
        ServerEntity("srv_lv_rig_01", "LV", "Latvia", "🇱🇻", "Riga", "rig1.protonplusvpn.net", "185.120.45.80", 51820, "lAt44ViArIg77GhIjKlMnOpQrStUvWxYz012345678=", 19, 16, 0.0f, 10000, false, false, 56.95f, 24.11f),
        ServerEntity("srv_lb_bey_01", "LB", "Lebanon", "🇱🇧", "Beirut", "bey1.protonplusvpn.net", "185.120.52.45", 51820, "lEb33AnOnBeI44KlMnOpQrStUvWxYz0123456789Ab=", 28, 20, 0.0f, 10000, false, false, 33.89f, 35.50f),
        ServerEntity("srv_lt_vno_01", "LT", "Lithuania", "🇱🇹", "Vilnius", "vno1.protonplusvpn.net", "185.120.45.90", 51820, "lIt55HuAnIaV88IjKlMnOpQrStUvWxYz0123456789=", 19, 15, 0.0f, 10000, false, false, 54.69f, 25.28f),
        ServerEntity("srv_lu_lux_01", "LU", "Luxembourg", "🇱🇺", "Luxembourg City", "lux1.protonplusvpn.net", "185.120.45.95", 51820, "lUx66EmBoUrG99KlMnOpQrStUvWxYz0123456789A=", 12, 18, 0.0f, 20000, true, false, 49.61f, 6.13f),

        // === M ===
        ServerEntity("srv_my_kul_01", "MY", "Malaysia", "🇲🇾", "Kuala Lumpur", "kul1.protonplusvpn.net", "103.253.147.20", 51820, "mAl99AySiAkU00JkLmNoPqRsTuVwXyZaBcDeFgHi=", 19, 23, 0.0f, 20000, true, false, 3.14f, 101.69f),
        ServerEntity("srv_mt_mla_01", "MT", "Malta", "🇲🇹", "Valletta", "mla1.protonplusvpn.net", "185.120.55.45", 51820, "mAl44TaVaLlE55JkLmNoPqRsTuVwXyZaBcDeFgHiJk=", 20, 15, 0.0f, 10000, false, false, 35.89f, 14.51f),
        ServerEntity("srv_mx_mex_01", "MX", "Mexico", "🇲🇽", "Mexico City", "mex1.protonplusvpn.net", "199.180.28.10", 51820, "mEx55IcoMeX66LmNoPqRsTuVwXyZaBcDeFgHiJk=", 21, 26, 0.0f, 20000, true, false, 19.43f, -99.13f),
        ServerEntity("srv_md_kiv_01", "MD", "Moldova", "🇲🇩", "Chișinău", "kiv1.protonplusvpn.net", "185.120.55.50", 51820, "mOl77DoVaKiV00MnOpQrStUvWxYz0123456789AbC=", 25, 14, 0.0f, 10000, false, false, 47.01f, 28.86f),
        ServerEntity("srv_mc_mon_01", "MC", "Monaco", "🇲🇨", "Monaco", "mon1.protonplusvpn.net", "185.120.55.55", 51820, "mOn88AcOmOn11OpQrStUvWxYz0123456789AbCdE=", 15, 12, 0.0f, 10000, false, false, 43.74f, 7.42f),
        ServerEntity("srv_mn_uln_01", "MN", "Mongolia", "🇲🇳", "Ulaanbaatar", "uln1.protonplusvpn.net", "95.173.150.50", 51820, "mOn66GoLiAuLn77OpQrStUvWxYz0123456789AbC=", 48, 15, 0.0f, 10000, false, false, 47.89f, 106.91f),
        ServerEntity("srv_me_pod_01", "ME", "Montenegro", "🇲🇪", "Podgorica", "pod1.protonplusvpn.net", "185.120.55.60", 51820, "mEn99TeNeGrO22QrStUvWxYz0123456789AbCdEf=", 23, 16, 0.0f, 10000, false, false, 42.43f, 19.26f),
        ServerEntity("srv_ma_cas_01", "MA", "Morocco", "🇲🇦", "Casablanca", "cas1.protonplusvpn.net", "185.120.56.50", 51820, "mOr55OcCoCaS66BcDeFgHiJkLmNoPqRsTuVwXyZaBc=", 25, 23, 0.0f, 10000, true, false, 33.57f, -7.58f),

        // === N ===
        ServerEntity("srv_np_ktm_01", "NP", "Nepal", "🇳🇵", "Kathmandu", "ktm1.protonplusvpn.net", "111.119.170.20", 51820, "nEp22AlKtM33VwXyZaBcDeFgHiJkLmNoPqRsTuVw=", 38, 22, 0.0f, 10000, false, false, 27.72f, 85.32f),
        ServerEntity("srv_nl_ams_01", "NL", "Netherlands", "🇳🇱", "Amsterdam", "ams1.protonplusvpn.net", "185.120.46.10", 51820, "aMs55TuVwXyZaBcDeFgHiJkLmNoPqRsTuVwXyZaBcDe=", 11, 23, 0.0f, 40000, true, false, 52.37f, 4.90f),
        ServerEntity("srv_nz_akl_01", "NZ", "New Zealand", "🇳🇿", "Auckland", "akl1.protonplusvpn.net", "139.130.12.80", 51820, "nEw11ZeAlAnD22LmNoPqRsTuVwXyZaBcDeFgHiJk=", 33, 17, 0.0f, 10000, false, false, -36.85f, 174.76f),
        ServerEntity("srv_ng_los_01", "NG", "Nigeria", "🇳🇬", "Lagos", "los1.protonplusvpn.net", "185.120.56.30", 51820, "nIg33ErIaLaG44BcDeFgHiJkLmNoPqRsTuVwXyZa=", 36, 26, 0.0f, 10000, false, false, 6.52f, 3.38f),
        ServerEntity("srv_mk_skp_01", "MK", "North Macedonia", "🇲🇰", "Skopje", "skp1.protonplusvpn.net", "185.120.55.75", 51820, "mAc44EdOnIaS55JkLmNoPqRsTuVwXyZaBcDeFgHiJk=", 22, 17, 0.0f, 10000, false, false, 41.99f, 21.42f),
        ServerEntity("srv_no_osl_01", "NO", "Norway", "🇳🇴", "Oslo", "osl1.protonplusvpn.net", "185.120.45.85", 51820, "nOr11WaYoSl33StUvWxYz0123456789AbCdEfGhI=", 15, 19, 0.0f, 20000, true, false, 59.91f, 10.75f),

        // === O ===
        ServerEntity("srv_om_mct_01", "OM", "Oman", "🇴🇲", "Muscat", "mct1.protonplusvpn.net", "185.120.52.85", 51820, "oMa22NmUsCaT33StUvWxYz0123456789AbCdEfGhIj=", 26, 20, 0.0f, 10000, false, false, 23.58f, 58.40f),

        // === P ===
        ServerEntity("srv_pk_isb_01", "PK", "Pakistan", "🇵🇰", "Islamabad", "isb1.protonplusvpn.net", "111.119.162.88", 51820, "iSb32DeFgHiJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNo=", 29, 31, 0.0f, 20000, true, false, 33.68f, 73.04f),
        ServerEntity("srv_pk_khi_01", "PK", "Pakistan", "🇵🇰", "Karachi", "khi1.protonplusvpn.net", "111.119.160.20", 51820, "kHi87ZaBcDeFgHiJkLmNoPqRsTuVwXyZaBcDeFgHiJk=", 31, 36, 0.0f, 20000, true, false, 24.86f, 67.00f),
        ServerEntity("srv_pk_lhe_01", "PK", "Pakistan", "🇵🇰", "Lahore", "lhe1.protonplusvpn.net", "111.119.164.12", 51820, "lHe11MnOpQrStUvWxYz0123456789AbCdEfGhIjKlMn=", 30, 34, 0.0f, 20000, true, false, 31.52f, 74.36f),
        ServerEntity("srv_pk_pew_01", "PK", "Pakistan", "🇵🇰", "Peshawar", "pew1.protonplusvpn.net", "111.119.165.10", 51820, "pEw33PeShAwA44ZaBcDeFgHiJkLmNoPqRsTuVwXyZa=", 32, 28, 0.0f, 10000, false, false, 34.01f, 71.52f),
        ServerEntity("srv_pk_rwp_01", "PK", "Pakistan", "🇵🇰", "Rawalpindi", "rwp1.protonplusvpn.net", "111.119.163.15", 51820, "rWp22RaWaLpI33ZaBcDeFgHiJkLmNoPqRsTuVwXyZa=", 29, 30, 0.0f, 10000, false, false, 33.59f, 73.04f),
        ServerEntity("srv_pa_pty_01", "PA", "Panama", "🇵🇦", "Panama City", "pty1.protonplusvpn.net", "199.180.30.70", 51820, "pAn66AmApTy77NoPqRsTuVwXyZaBcDeFgHiJkLm=", 27, 21, 0.0f, 10000, false, false, 8.98f, -79.52f),
        ServerEntity("srv_py_asu_01", "PY", "Paraguay", "🇵🇾", "Asunción", "asu1.protonplusvpn.net", "199.180.30.85", 51820, "pAr33AgUaYaS44PqRsTuVwXyZaBcDeFgHiJkLmNoP=", 34, 18, 0.0f, 10000, false, false, -25.26f, -57.57f),
        ServerEntity("srv_pe_lim_01", "PE", "Peru", "🇵🇪", "Lima", "lim1.protonplusvpn.net", "199.180.30.80", 51820, "pEr77ULiMa88PqRsTuVwXyZaBcDeFgHiJkLmNoP=", 32, 20, 0.0f, 10000, false, false, -12.05f, -77.04f),
        ServerEntity("srv_ph_mnl_01", "PH", "Philippines", "🇵🇭", "Manila", "mnl1.protonplusvpn.net", "103.253.149.30", 51820, "pHi22LiPpInE33NoPqRsTuVwXyZaBcDeFgHiJkLm=", 26, 25, 0.0f, 10000, false, false, 14.60f, 120.98f),
        ServerEntity("srv_pl_waw_01", "PL", "Poland", "🇵🇱", "Warsaw", "waw1.protonplusvpn.net", "185.120.49.10", 51820, "pOl22AnDwAw44TuVwXyZaBcDeFgHiJkLmNoPqRsTu=", 16, 25, 0.0f, 20000, true, false, 52.23f, 21.01f),
        ServerEntity("srv_pt_lis_01", "PT", "Portugal", "🇵🇹", "Lisbon", "lis1.protonplusvpn.net", "185.120.49.20", 51820, "pOr33TuGaLlIs55VwXyZaBcDeFgHiJkLmNoPqRsT=", 18, 22, 0.0f, 10000, true, false, 38.72f, -9.14f),

        // === Q ===
        ServerEntity("srv_qa_doh_01", "QA", "Qatar", "🇶🇦", "Doha", "doh1.protonplusvpn.net", "185.120.52.70", 51820, "qAt22ArDoHaQ33StUvWxYz0123456789AbCdEfGhIj=", 22, 27, 0.0f, 30000, true, false, 25.28f, 51.53f),

        // === R ===
        ServerEntity("srv_ro_otp_01", "RO", "Romania", "🇷🇴", "Bucharest", "otp1.protonplusvpn.net", "185.120.49.30", 51820, "rOm44AnIaOtP66XyZaBcDeFgHiJkLmNoPqRsTuVw=", 20, 21, 0.0f, 10000, true, false, 44.43f, 26.10f),
        ServerEntity("srv_ru_mow_01", "RU", "Russia", "🇷🇺", "Moscow", "mow1.protonplusvpn.net", "95.173.136.50", 51820, "mOw98ZkL21PqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuV=", 24, 29, 0.0f, 20000, true, false, 55.76f, 37.62f),
        ServerEntity("srv_ru_ovb_01", "RU", "Russia", "🇷🇺", "Novosibirsk", "ovb1.protonplusvpn.net", "95.173.140.22", 51820, "oVb76AbCdEfGhIjKlMnOpQrStUvWxYz0123456789Ab=", 39, 18, 0.05f, 10000, false, false, 55.01f, 82.93f),
        ServerEntity("srv_ru_led_01", "RU", "Russia", "🇷🇺", "Saint Petersburg", "led1.protonplusvpn.net", "95.173.138.12", 51820, "lEd43XyZaBcDeFgHiJkLmNoPqRsTuVwXyZaBcDeFgHi=", 25, 23, 0.0f, 20000, true, false, 59.93f, 30.34f),

        // === S ===
        ServerEntity("srv_sa_ruh_01", "SA", "Saudi Arabia", "🇸🇦", "Riyadh", "ruh1.protonplusvpn.net", "185.120.52.60", 51820, "sAu33DiArAbIa44XyZaBcDeFgHiJkLmNoPqRsTuV=", 24, 28, 0.0f, 20000, true, false, 24.71f, 46.68f),
        ServerEntity("srv_sa_jed_01", "SA", "Saudi Arabia", "🇸🇦", "Jeddah", "jed1.protonplusvpn.net", "185.120.52.65", 51820, "jEd22SaUdIje33XyZaBcDeFgHiJkLmNoPqRsTuVwXy=", 25, 24, 0.0f, 20000, false, false, 21.48f, 39.19f),
        ServerEntity("srv_rs_beg_01", "RS", "Serbia", "🇷🇸", "Belgrade", "beg1.protonplusvpn.net", "185.120.55.70", 51820, "sEr55BiAbEg77ZaBcDeFgHiJkLmNoPqRsTuVwXy=", 21, 18, 0.0f, 10000, false, false, 44.79f, 20.45f),
        ServerEntity("srv_sg_sin_01", "SG", "Singapore", "🇸🇬", "Singapore", "sin1.protonplusvpn.net", "103.253.144.5", 51820, "sIn65FgHiJkLmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPq=", 10, 18, 0.0f, 40000, true, false, 1.35f, 103.82f),
        ServerEntity("srv_sk_bts_01", "SK", "Slovakia", "🇸🇰", "Bratislava", "bts1.protonplusvpn.net", "185.120.49.40", 51820, "sLo66VaKiAbTs88BcDeFgHiJkLmNoPqRsTuVwXyZ=", 17, 19, 0.0f, 10000, false, false, 48.15f, 17.11f),
        ServerEntity("srv_si_lju_01", "SI", "Slovenia", "🇸🇮", "Ljubljana", "lju1.protonplusvpn.net", "185.120.49.50", 51820, "sLv77EnIaLjU99CdEfGhIjKlMnOpQrStUvWxYz0=", 16, 17, 0.0f, 10000, false, false, 46.06f, 14.51f),
        ServerEntity("srv_za_jnb_01", "ZA", "South Africa", "🇿🇦", "Johannesburg", "jnb1.protonplusvpn.net", "185.120.56.40", 51820, "sOu44ThAfRiCa55DeFgHiJkLmNoPqRsTuVwXyZaB=", 37, 24, 0.0f, 20000, true, false, -26.20f, 28.05f),
        ServerEntity("srv_za_cpt_01", "ZA", "South Africa", "🇿🇦", "Cape Town", "cpt1.protonplusvpn.net", "185.120.56.45", 51820, "cPt33CaPeToW44DeFgHiJkLmNoPqRsTuVwXyZaBcDe=", 38, 20, 0.0f, 10000, false, false, -33.92f, 18.42f),
        ServerEntity("srv_kr_icn_01", "KR", "South Korea", "🇰🇷", "Seoul", "icn1.protonplusvpn.net", "133.242.24.50", 51820, "sOu33ThKoReA44PqRsTuVwXyZaBcDeFgHiJkLmNo=", 15, 23, 0.0f, 30000, true, false, 37.57f, 126.98f),
        ServerEntity("srv_es_mad_01", "ES", "Spain", "🇪🇸", "Madrid", "mad1.protonplusvpn.net", "185.120.49.60", 51820, "sPa88InMaD00DeFgHiJkLmNoPqRsTuVwXyZaBcD=", 16, 27, 0.0f, 20000, true, false, 40.42f, -3.70f),
        ServerEntity("srv_es_bcn_01", "ES", "Spain", "🇪🇸", "Barcelona", "bcn1.protonplusvpn.net", "185.120.49.65", 51820, "bCn33BaRcElO44DeFgHiJkLmNoPqRsTuVwXyZaBcDe=", 15, 22, 0.0f, 20000, false, false, 41.38f, 2.17f),
        ServerEntity("srv_lk_cmb_01", "LK", "Sri Lanka", "🇱🇰", "Colombo", "cmb1.protonplusvpn.net", "103.253.150.10", 51820, "sRi44LaNkAcMb55ZaBcDeFgHiJkLmNoPqRsTuVwX=", 34, 24, 0.0f, 10000, false, false, 6.93f, 79.86f),
        ServerEntity("srv_se_arn_01", "SE", "Sweden", "🇸🇪", "Stockholm", "arn1.protonplusvpn.net", "185.120.49.70", 51820, "sWe99DeNaRn11EfGhIjKlMnOpQrStUvWxYz012=", 15, 20, 0.0f, 20000, true, false, 59.33f, 18.07f),
        ServerEntity("srv_ch_zrh_01", "CH", "Switzerland", "🇨🇭", "Zurich", "zrh1.protonplusvpn.net", "185.120.47.15", 51820, "zRh66UvWxYz0123456789AbCdEfGhIjKlMnOpQrStUv=", 11, 16, 0.0f, 30000, true, false, 47.38f, 8.54f),
        ServerEntity("srv_ch_gva_01", "CH", "Switzerland", "🇨🇭", "Geneva", "gva1.protonplusvpn.net", "185.120.47.20", 51820, "gVa33GeNeVaS44UvWxYz0123456789AbCdEfGhIjKl=", 12, 19, 0.0f, 20000, false, false, 46.20f, 6.14f),

        // === T ===
        ServerEntity("srv_tw_tpe_01", "TW", "Taiwan", "🇹🇼", "Taipei", "tpe1.protonplusvpn.net", "103.253.142.20", 51820, "tAi44WaNtPe55RsTuVwXyZaBcDeFgHiJkLmNoPqR=", 16, 21, 0.0f, 20000, true, false, 25.03f, 121.57f),
        ServerEntity("srv_th_bkk_01", "TH", "Thailand", "🇹🇭", "Bangkok", "bkk1.protonplusvpn.net", "103.253.143.10", 51820, "tHa55IlAnDbK66TuVwXyZaBcDeFgHiJkLmNoPqRs=", 22, 24, 0.0f, 20000, false, false, 13.76f, 100.50f),
        ServerEntity("srv_tn_tun_01", "TN", "Tunisia", "🇹🇳", "Tunis", "tun1.protonplusvpn.net", "185.120.56.80", 51820, "tUn44IsIaTuN55BcDeFgHiJkLmNoPqRsTuVwXyZaBc=", 26, 19, 0.0f, 10000, false, false, 36.80f, 10.18f),
        ServerEntity("srv_tr_ist_01", "TR", "Turkey", "🇹🇷", "Istanbul", "ist1.protonplusvpn.net", "185.120.53.10", 51820, "tUr55KeYiSt66BcDeFgHiJkLmNoPqRsTuVwXyZaB=", 22, 29, 0.0f, 20000, true, false, 41.01f, 28.98f),
        ServerEntity("srv_tr_ank_01", "TR", "Turkey", "🇹🇷", "Ankara", "ank1.protonplusvpn.net", "185.120.53.20", 51820, "aNk33AnKaRaT44BcDeFgHiJkLmNoPqRsTuVwXyZaBc=", 23, 22, 0.0f, 20000, false, false, 39.93f, 32.85f),

        // === U ===
        ServerEntity("srv_ua_kbp_01", "UA", "Ukraine", "🇺🇦", "Kyiv", "kbp1.protonplusvpn.net", "185.120.49.80", 51820, "uKr11AiNeKbP22FgHiJkLmNoPqRsTuVwXyZaBcDe=", 23, 24, 0.0f, 10000, true, false, 50.45f, 30.52f),
        ServerEntity("srv_ae_dxb_01", "AE", "United Arab Emirates", "🇦🇪", "Dubai", "dxb1.protonplusvpn.net", "185.120.52.88", 51820, "dXb77NoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuVwXy=", 23, 31, 0.0f, 30000, true, false, 25.20f, 55.27f),
        ServerEntity("srv_ae_auh_01", "AE", "United Arab Emirates", "🇦🇪", "Abu Dhabi", "auh1.protonplusvpn.net", "185.120.52.90", 51820, "aUh33AbUdHaB44NoPqRsTuVwXyZaBcDeFgHiJkLmNo=", 24, 26, 0.0f, 20000, true, false, 24.45f, 54.37f),
        ServerEntity("srv_gb_lon_01", "GB", "United Kingdom", "🇬🇧", "London", "lon1.protonplusvpn.net", "185.120.48.5", 51820, "lOn99LmNoPqRsTuVwXyZaBcDeFgHiJkLmNoPqRsTuVw=", 12, 30, 0.0f, 30000, true, false, 51.51f, -0.13f),
        ServerEntity("srv_gb_man_01", "GB", "United Kingdom", "🇬🇧", "Manchester", "man1.protonplusvpn.net", "185.120.48.20", 51820, "mAn44StUvWxYz0123456789AbCdEfGhIjKlMnOpQrSt=", 14, 25, 0.0f, 20000, false, false, 53.48f, -2.24f),
        ServerEntity("srv_us_ash_01", "US", "United States", "🇺🇸", "Ashburn (VA)", "iad1.protonplusvpn.net", "199.180.12.44", 51820, "aBc1DeF2GhI3JkL4MnO5PqR6StU7VwX8Yz9AbCdEfGh=", 13, 27, 0.0f, 40000, true, false, 39.04f, -77.49f),
        ServerEntity("srv_us_lax_01", "US", "United States", "🇺🇸", "Los Angeles (CA)", "lax1.protonplusvpn.net", "199.180.14.88", 51820, "lAx33QrStUvWxYz0123456789AbCdEfGhIjKlMnOpQr=", 18, 25, 0.0f, 30000, true, false, 34.05f, -118.24f),
        ServerEntity("srv_us_nyc_01", "US", "United States", "🇺🇸", "New York (NY)", "nyc1.protonplusvpn.net", "199.180.12.50", 51820, "nYc22OpQrStUvWxYz0123456789AbCdEfGhIjKlMnOp=", 14, 29, 0.0f, 30000, true, false, 40.71f, -74.01f),
        ServerEntity("srv_us_mia_01", "US", "United States", "🇺🇸", "Miami (FL)", "mia1.protonplusvpn.net", "199.180.12.60", 51820, "mIa33MiAmIfL44OpQrStUvWxYz0123456789AbCdEf=", 16, 26, 0.0f, 30000, true, false, 25.76f, -80.19f),
        ServerEntity("srv_us_ord_01", "US", "United States", "🇺🇸", "Chicago (IL)", "ord1.protonplusvpn.net", "199.180.12.70", 51820, "oRd44ChIcAgO55OpQrStUvWxYz0123456789AbCdEf=", 15, 25, 0.0f, 30000, true, false, 41.87f, -87.62f),
        ServerEntity("srv_us_dfw_01", "US", "United States", "🇺🇸", "Dallas (TX)", "dfw1.protonplusvpn.net", "199.180.12.80", 51820, "dFw55DaLlAsT66OpQrStUvWxYz0123456789AbCdEf=", 16, 24, 0.0f, 30000, false, false, 32.77f, -96.79f),
        ServerEntity("srv_us_sea_01", "US", "United States", "🇺🇸", "Seattle (WA)", "sea1.protonplusvpn.net", "199.180.14.90", 51820, "sEa66SeAtTlE77QrStUvWxYz0123456789AbCdEfGh=", 17, 22, 0.0f, 20000, true, false, 47.60f, -122.33f),
        ServerEntity("srv_uy_mvd_01", "UY", "Uruguay", "🇺🇾", "Montevideo", "mvd1.protonplusvpn.net", "199.180.30.90", 51820, "uRu88GuAyMvD99StUvWxYz0123456789AbCdEfGh=", 35, 19, 0.0f, 10000, false, false, -34.90f, -56.16f),
        ServerEntity("srv_uz_tas_01", "UZ", "Uzbekistan", "🇺🇿", "Tashkent", "tas1.protonplusvpn.net", "95.173.150.60", 51820, "uZb77EkIsTaN88QrStUvWxYz0123456789AbCdE=", 35, 18, 0.0f, 10000, false, false, 41.30f, 69.24f),

        // === V ===
        ServerEntity("srv_ve_ccs_01", "VE", "Venezuela", "🇻🇪", "Caracas", "ccs1.protonplusvpn.net", "199.180.30.95", 51820, "vEn99EzUeLaC00UvWxYz0123456789AbCdEfGhIj=", 36, 22, 0.0f, 10000, false, false, 10.48f, -66.90f),
        ServerEntity("srv_vn_han_01", "VN", "Vietnam", "🇻🇳", "Hanoi", "han1.protonplusvpn.net", "103.253.145.40", 51820, "vIe66TnAmHaN77VwXyZaBcDeFgHiJkLmNoPqRsTu=", 23, 26, 0.0f, 20000, true, false, 21.03f, 105.85f)
    ).sortedWith(compareBy({ it.countryName }, { it.cityName }))
}
