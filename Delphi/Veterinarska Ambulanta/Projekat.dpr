program Projekat;





uses
  System.StartUpCopy,
  FMX.Forms,
  LoginForma in 'LoginForma.pas' {frmLogIn},
  Registracija in 'Registracija.pas' {frmSignUp},
  Vlasnik_GlavnaForma in 'Vlasnik_GlavnaForma.pas' {frmGlavniMeni},
  DodavanjeLjubimca in 'DodavanjeLjubimca.pas' {frmDodavanjeLjubimca},
  Main in 'Main.pas' {frmMain},
  AdresaKorisnika in 'AdresaKorisnika.pas' {frmAdresaKorisnika},
  SpisakZivotinja in 'SpisakZivotinja.pas' {frmSpisakZivotinja},
  Zakazivanje_Termina in 'Zakazivanje_Termina.pas' {frmZakazivanjeTermina},
  LoginZaposleni in 'LoginZaposleni.pas' {frmLogInZaposleni},
  Termini_Veterinar in 'Termini_Veterinar.pas' {frmTerminiVeterinar},
  Pregled in 'Pregled.pas' {frmPregled},
  PrepisaniLekovi in 'PrepisaniLekovi.pas' {frmPrepisaniLekovi},
  SpisakZaposlenih in 'SpisakZaposlenih.pas' {frmZaposleni},
  Ljubimac_Pregledi in 'Ljubimac_Pregledi.pas' {frmljubimac_pregledi},
  Pregled_Lekovi in 'Pregled_Lekovi.pas' {frmpregled_lekovi},
  SpisakTermina in 'SpisakTermina.pas' {frmSpisakTermina},
  CallCentar in 'CallCentar.pas' {CCForm1},
  CallCentarOdgovor in 'CallCentarOdgovor.pas' {CCForm2},
  CallCentarOdgovorVeterinara in 'CallCentarOdgovorVeterinara.pas' {CCForm3},
  SpisakPromocija in 'SpisakPromocija.pas' {frmPromocije},
  DodavanjePromocije in 'DodavanjePromocije.pas' {frmdodavanjepromocije},
  AdresaZaposlenih in 'AdresaZaposlenih.pas' {frmAdresaZaposlenih},
  Inventar in 'Inventar.pas' {frmInventar},
  Nabavka in 'Nabavka.pas' {frmNabavka},
  NoviZaposleni in 'NoviZaposleni.pas' {frmNoviZaposleni},
  Porucivanje in 'Porucivanje.pas' {frmPoruci},
  SpisakDobavljaca in 'SpisakDobavljaca.pas' {frmDobavljaci},
  Utovar in 'Utovar.pas' {frmutovar},
  SpisakPlata in 'SpisakPlata.pas' {frmPlate},
  DodajTransakciju in 'DodajTransakciju.pas' {frmdodajtransakciju},
  PlateZaposlenih in 'PlateZaposlenih.pas' {frmPlateFinansije},
  SpisakZaposlenih_Finansije in 'SpisakZaposlenih_Finansije.pas' {frmZaposleniFinansije},
  Transakcije in 'Transakcije.pas' {frmTransakcije},
  Finansije_GlavnaForma in 'Finansije_GlavnaForma.pas' {frmfinansijemeni},
  UplataPlata in 'UplataPlata.pas' {frmDodajUplatu},
  Obavestenja in 'Obavestenja.pas' {frmObavestenja},
  PotrebniLekovi in 'PotrebniLekovi.pas' {frmPotrebniLekovi},
  StariPregled in 'StariPregled.pas' {frmStariPregled},
  StariLekovi in 'StariLekovi.pas' {frmStariLekovi},
  Dostave in 'Dostave.pas' {frmDostave},
  Lekar_PotrebniLekovi in 'Lekar_PotrebniLekovi.pas' {frmLekar_Lekovi},
  BivsaPromocija in 'BivsaPromocija.pas' {frmBivsaPromocija},
  Vlasnik_Promocija in 'Vlasnik_Promocija.pas' {frmVlasnikPromocije},
  Zaposleni_Promocija in 'Zaposleni_Promocija.pas' {frmZaposleniPromocije},
  PromocijaOpis in 'PromocijaOpis.pas' {frmEditPromocija},
  PromocijaLista in 'PromocijaLista.pas' {frmListaPromocija},
  Finansije_Dostava in 'Finansije_Dostava.pas' {frmfinansijeDostava},
  Finansije_Obavestenja in 'Finansije_Obavestenja.pas' {frmfinansijeObavestenja},
  CallCentarPregledPitanja in 'CallCentarPregledPitanja.pas' {CCForm4},
  Ljubimci_Vakcine in 'Ljubimci_Vakcine.pas' {frmLjubimciVakcine},
  SpisakVakcina in 'SpisakVakcina.pas' {frmspisakVakcina},
  VeterinarVakcine in 'VeterinarVakcine.pas' {frmVeterinarVakcine},
  StareVakcine in 'StareVakcine.pas' {frmStareVakcine},
  Finansije_Transakcije in 'Finansije_Transakcije.pas' {frmfinansijeTransakcije},
  Finansije_Plate in 'Finansije_Plate.pas' {frmFinansijePlate};

{$R *.res}

begin
  Application.Initialize;
  Application.CreateForm(TfrmLogIn, frmLogIn);
  Application.CreateForm(TfrmMain, frmMain);
  Application.CreateForm(TfrmSignUp, frmSignUp);
  Application.CreateForm(TfrmGlavniMeni, frmGlavniMeni);
  Application.CreateForm(TfrmDodavanjeLjubimca, frmDodavanjeLjubimca);
  Application.CreateForm(TfrmAdresaKorisnika, frmAdresaKorisnika);
  Application.CreateForm(TfrmSpisakZivotinja, frmSpisakZivotinja);
  Application.CreateForm(TfrmZakazivanjeTermina, frmZakazivanjeTermina);
  Application.CreateForm(TfrmLogInZaposleni, frmLogInZaposleni);
  Application.CreateForm(TfrmTerminiVeterinar, frmTerminiVeterinar);
  Application.CreateForm(TfrmPregled, frmPregled);
  Application.CreateForm(TfrmPrepisaniLekovi, frmPrepisaniLekovi);
  Application.CreateForm(TfrmZaposleni, frmZaposleni);
  Application.CreateForm(Tfrmljubimac_pregledi, frmljubimac_pregledi);
  Application.CreateForm(Tfrmpregled_lekovi, frmpregled_lekovi);
  Application.CreateForm(TfrmSpisakTermina, frmSpisakTermina);
  Application.CreateForm(TCCForm1, CCForm1);
  Application.CreateForm(TCCForm2, CCForm2);
  Application.CreateForm(TCCForm3, CCForm3);
  Application.CreateForm(TfrmPromocije, frmPromocije);
  Application.CreateForm(Tfrmdodavanjepromocije, frmdodavanjepromocije);
  Application.CreateForm(TfrmAdresaZaposlenih, frmAdresaZaposlenih);
  Application.CreateForm(TfrmInventar, frmInventar);
  Application.CreateForm(TfrmNabavka, frmNabavka);
  Application.CreateForm(TfrmNoviZaposleni, frmNoviZaposleni);
  Application.CreateForm(TfrmPoruci, frmPoruci);
  Application.CreateForm(TfrmDobavljaci, frmDobavljaci);
  Application.CreateForm(Tfrmutovar, frmutovar);
  Application.CreateForm(TfrmPlate, frmPlate);
  Application.CreateForm(Tfrmdodajtransakciju, frmdodajtransakciju);
  Application.CreateForm(TfrmDodajTransakciju, frmDodajTransakciju);
  Application.CreateForm(TfrmPlateFinansije, frmPlateFinansije);
  Application.CreateForm(TfrmDodajTransakciju, frmDodajTransakciju);
  Application.CreateForm(TfrmPlateFinansije, frmPlateFinansije);
  Application.CreateForm(TfrmZaposleniFinansije, frmZaposleniFinansije);
  Application.CreateForm(TfrmTransakcije, frmTransakcije);
  Application.CreateForm(Tfrmfinansijemeni, frmfinansijemeni);
  Application.CreateForm(TfrmDodajUplatu, frmDodajUplatu);
  Application.CreateForm(TfrmObavestenja, frmObavestenja);
  Application.CreateForm(TfrmPotrebniLekovi, frmPotrebniLekovi);
  Application.CreateForm(TfrmStariPregled, frmStariPregled);
  Application.CreateForm(TfrmStariLekovi, frmStariLekovi);
  Application.CreateForm(TfrmDostave, frmDostave);
  Application.CreateForm(TfrmLekar_Lekovi, frmLekar_Lekovi);
  Application.CreateForm(TfrmBivsaPromocija, frmBivsaPromocija);
  Application.CreateForm(TfrmVlasnikPromocije, frmVlasnikPromocije);
  Application.CreateForm(TfrmZaposleniPromocije, frmZaposleniPromocije);
  Application.CreateForm(TfrmEditPromocija, frmEditPromocija);
  Application.CreateForm(TfrmListaPromocija, frmListaPromocija);
  Application.CreateForm(TfrmfinansijeDostava, frmfinansijeDostava);
  Application.CreateForm(TfrmfinansijeObavestenja, frmfinansijeObavestenja);
  Application.CreateForm(TCCForm4, CCForm4);
  Application.CreateForm(TfrmLjubimciVakcine, frmLjubimciVakcine);
  Application.CreateForm(TfrmspisakVakcina, frmspisakVakcina);
  Application.CreateForm(TfrmVeterinarVakcine, frmVeterinarVakcine);
  Application.CreateForm(TfrmStareVakcine, frmStareVakcine);
  Application.CreateForm(TfrmfinansijeTransakcije, frmfinansijeTransakcije);
  Application.CreateForm(TfrmFinansijePlate, frmFinansijePlate);
  Application.Run;
end.
