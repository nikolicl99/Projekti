unit Obavestenja;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, System.Rtti,
  FMX.Grid.Style, FMX.Grid, FMX.Controls.Presentation, FMX.ScrollBox,
  FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Param,
  FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf, FireDAC.DApt.Intf,
  FireDAC.Stan.Async, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client, FMX.StdCtrls, FMX.Objects;

type
  TfrmObavestenja = class(TForm)
    Termini: TStringGrid;
    Ljubimac: TStringColumn;
    Tip: TStringColumn;
    DatumPregleda: TStringColumn;
    DoPregleda: TStringColumn;
    Vreme: TStringColumn;
    Lekovi: TStringGrid;
    Ljubimac_Lek: TStringColumn;
    Tip_Lek: TStringColumn;
    Datum_Lek: TStringColumn;
    Lek: TStringColumn;
    Kolicina: TIntegerColumn;
    Nazad: TButton;
    Cena_Komad: TIntegerColumn;
    Lekovi_but: TRadioButton;
    Termini_but: TRadioButton;
    Navbar: TImage;
    Forma: TPanel;
    StyleBook: TStyleBook;
    CallCentar: TRadioButton;
    CallCentar_Grid: TStringGrid;
    Tip_Pitanje_col: TStringColumn;
    Datum_col: TStringColumn;
    Pitanje: TStringColumn;
    Vreme_col: TStringColumn;
    IDPitanja: TIntegerColumn;
    Vakcine_but: TRadioButton;
    Vakcine: TStringGrid;
    Ljubimac_vac: TStringColumn;
    Vakcina: TStringColumn;
    Revakcinacija: TStringColumn;
    DoPregleda_vac: TStringColumn;
    procedure FormCreate(Sender: TObject);
    procedure NazadClick(Sender: TObject);
    procedure Termini_butChange(Sender: TObject);
    procedure Lekovi_butChange(Sender: TObject);
    procedure CallCentarChange(Sender: TObject);
    procedure Vakcine_butChange(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmObavestenja: TfrmObavestenja;

implementation

{$R *.fmx}
uses Main,Vlasnik_GlavnaForma, LoginForma;



procedure TfrmObavestenja.FormCreate(Sender: TObject);
var
  Query: TFDQuery;
  MyQuery: TFDQuery;
  Kveri: TFDQuery;
  MojKveri: TFDQuery;
  Row: Integer;
  DanasnjiDatum: String;
begin
   Query := TFDQuery.Create(nil);
   DanasnjiDatum := FormatDateTime('dd/mm/yyyy', Now);
   try
     Query.Connection := GlobalConnection;
  Query.SQL.Text :=
  'SELECT Termin.Ljubimac, Termin.Klijent, Termin.Datum, Termin.Vreme, Ljubimci.IDLjubimca, Ljubimci.ImeLjubimca, Tipovi_Ljubimaca.IDTipa, Tipovi_Ljubimaca.Ime_Tipa, ' +
  'julianday(substr(Termin.Datum, 7, 4) || "-" || substr(Termin.Datum, 4, 2) || "-" || substr(Termin.Datum, 1, 2)) - julianday(:Danasnji) AS RazlikaDani ' +
  'FROM Termin ' +
  'JOIN Ljubimci ON Termin.Ljubimac = Ljubimci.IDLjubimca ' +
  'JOIN rasa_ljubimca ON ljubimci.rasa = rasa_ljubimca.idrase ' +
  'JOIN tipovi_ljubimaca ON rasa_ljubimca.tipljubimca = tipovi_ljubimaca.idtipa ' +
  'WHERE Termin.Klijent = :VlasnikID ' +
  'AND julianday(substr(Termin.Datum, 7, 4) || "-" || substr(Termin.Datum, 4, 2) || "-" || substr(Termin.Datum, 1, 2)) >= julianday(:Danasnji) ' +
  'AND termin.idtermina NOT IN (SELECT termin FROM pregled)' +
  'AND RazlikaDani <=7 '+
  'ORDER BY RazlikaDani ASC';

Query.ParamByName('VlasnikID').AsInteger := frmlogin.UlogovaniVlasnikID;
Query.ParamByName('Danasnji').AsString := FormatDateTime('yyyy-mm-dd', StrToDate(DanasnjiDatum));

      Query.Open;
    Termini.RowCount := Query.RecordCount;

    Row := 0;
    while not Query.Eof do
    begin
      Termini.Cells[0, Row] := Query.FieldByName('ImeLjubimca').AsString;
      Termini.Cells[1, Row] := Query.FieldByName('Ime_Tipa').AsString;
      Termini.Cells[2, Row] := Query.FieldByName('Datum').AsString;
      Termini.Cells[3, Row] := Query.FieldByName('Vreme').AsString;
      Termini.Cells[4, Row] := inttostr(Trunc(strtodate(Query.FieldByName('Datum').AsString) - strtodate(DanasnjiDatum)));
      Query.Next;
      Inc(Row);
    end;
   finally
      Query.Free;
   end;
   MyQuery := TFDQuery.Create(nil);
   try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text :=
  'SELECT inventar.id, inventar.ime, prepisani_lekovi.kolicina_leka, inventar.cena_po_komadu, inventar.kolicina, '+
  'inventar.potrebna_kolicina, termin.datum, termin.klijent, termin.Ljubimac, Ljubimci.imeljubimca, tipovi_ljubimaca.IDTipa, tipovi_ljubimaca.Ime_Tipa, ' +
  'prepisani_lekovi.idpregleda, pregled.termin, pregled.idpregleda '+
  'FROM inventar ' +
  'JOIN prepisani_lekovi ON inventar.id = prepisani_lekovi.idleka ' +
  'JOIN pregled ON prepisani_lekovi.idpregleda = pregled.idpregleda '+
  'JOIN termin ON pregled.termin = termin.idtermina '+
  'JOIN ljubimci ON termin.ljubimac = ljubimci.idljubimca '+
  'JOIN rasa_ljubimca ON ljubimci.rasa = rasa_ljubimca.idrase ' +
  'JOIN tipovi_ljubimaca ON rasa_ljubimca.tipljubimca = tipovi_ljubimaca.idtipa ' +
  'WHERE termin.klijent = :VlasnikID '+
  'AND inventar.kolicina < inventar.potrebna_kolicina ' +
  'AND Tip = ''LEKOVI''';

MyQuery.ParamByName('VlasnikID').AsInteger := frmlogin.UlogovaniVlasnikID;
MyQuery.Open;


Lekovi.RowCount := MyQuery.RecordCount;

Row := 0;
while not MyQuery.Eof do
begin
  Lekovi.Cells[0, Row] := MyQuery.FieldByName('imeljubimca').AsString;
  Lekovi.Cells[1, Row] := MyQuery.FieldByName('Ime_Tipa').AsString;
  Lekovi.Cells[2, Row] := MyQuery.FieldByName('datum').AsString;
  Lekovi.Cells[3, Row] := MyQuery.FieldByName('ime').AsString;
  Lekovi.Cells[4, Row] := MyQuery.FieldByName('kolicina_leka').AsString;
  Lekovi.Cells[5, Row] := MyQuery.FieldByName('cena_po_komadu').AsString;
  MyQuery.Next;
  Inc(Row);
end;
  finally
    MyQuery.Free;
  end;
  Kveri := TFDQuery.Create(nil);
  try
   Kveri.Connection := GlobalConnection;
    Query.SQL.Text :=
  'SELECT idpitanja, idklijent, tip_pitanja, datum_prijema, vreme_prijema, pitanje '+
  'FROM call_centar ' +
  'WHERE IDKlijent = :VlasnikID '+
  'AND odgovor IS NULL';

    Kveri.ParamByName('VlasnikID').AsInteger := frmlogin.UlogovaniVlasnikID;
    Kveri.Open;


    CallCentar_Grid.RowCount := Kveri.RecordCount;

    Row := 0;
    while not Kveri.Eof do
    begin
      CallCentar_Grid.Cells[0, Row] := Kveri.FieldByName('idpitanja').AsString;
      CallCentar_Grid.Cells[1, Row] := Kveri.FieldByName('tip_pitanja').AsString;
      CallCentar_Grid.Cells[2, Row] := Kveri.FieldByName('datum_prijema').AsString;
      CallCentar_Grid.Cells[3, Row] := Kveri.FieldByName('vreme_prijema').AsString;
      CallCentar_Grid.Cells[4, Row] := Kveri.FieldByName('pitanje').AsString;
      Kveri.Next;
      Inc(Row);
    end;
  finally
      Kveri.free;
  end;

  MojKveri := TFDQuery.Create(nil);
  try
   MojKveri.Connection := GlobalConnection;
    MojKveri.SQL.Text :=
  'SELECT Klijent_Vakcinacija.*, Vakcine.*, Ljubimci.*,  '+
  'julianday(substr(Klijent_Vakcinacija.Revakcinacija, 7, 4) || "-" || substr(Klijent_Vakcinacija.Revakcinacija, 4, 2) || "-" || substr(Klijent_Vakcinacija.Revakcinacija, 1, 2)) - julianday(:Danasnji) AS RazlikaDani ' +
  'FROM Klijent_Vakcinacija JOIN Vakcine ON Klijent_Vakcinacija.Vakcina = Vakcine.IDVakcine ' +
  'JOIN Ljubimci ON Klijent_Vakcinacija.Klijent = Ljubimci.IDLjubimca ' +
  'WHERE Klijent_Vakcinacija.Klijent = :VlasnikID '+
  'AND RazlikaDani <=7 '+
  'AND julianday(substr(Klijent_Vakcinacija.Revakcinacija, 7, 4) || "-" || substr(Klijent_Vakcinacija.Revakcinacija, 4, 2) || "-" || substr(Klijent_Vakcinacija.Revakcinacija, 1, 2)) >= julianday(:Danasnji) ' +
  'ORDER BY RazlikaDani ASC';

    MojKveri.ParamByName('VlasnikID').AsInteger := frmlogin.UlogovaniVlasnikID;
    MojKveri.ParamByName('Danasnji').AsString := FormatDateTime('yyyy-mm-dd', StrToDate(DanasnjiDatum));
    MojKveri.Open;


    Vakcine.RowCount := MojKveri.RecordCount;

    Row := 0;
    while not MojKveri.Eof do
    begin
      Vakcine.Cells[0, Row] := MojKveri.FieldByName('imeljubimca').AsString;
      Vakcine.Cells[1, Row] := MojKveri.FieldByName('imevakcine').AsString;
      Vakcine.Cells[2, Row] := MojKveri.FieldByName('revakcinacija').AsString;
      Vakcine.Cells[3, Row] := inttostr(Trunc(strtodate(MojKveri.FieldByName('revakcinacija').AsString) - strtodate(DanasnjiDatum)));
      MojKveri.Next;
      Inc(Row);
    end;
  finally
      MojKveri.free;
  end;
end;

procedure TfrmObavestenja.Lekovi_butChange(Sender: TObject);
begin
Lekovi.Visible := True;
Termini.Visible := False;
CallCentar_Grid.Visible := False;
Vakcine.Visible := False;
end;

procedure TfrmObavestenja.NazadClick(Sender: TObject);
begin
frmglavnimeni.show;
self.hide;
end;

procedure TfrmObavestenja.Termini_butChange(Sender: TObject);
begin
Termini.Visible := True;
Lekovi.Visible := False;
CallCentar_Grid.Visible := False;
Vakcine.Visible := False;
end;

procedure TfrmObavestenja.Vakcine_butChange(Sender: TObject);
begin
Lekovi.Visible := False;
Termini.Visible := False;
CallCentar_Grid.Visible := False;
Vakcine.Visible := True;
end;

procedure TfrmObavestenja.CallCentarChange(Sender: TObject);
begin
Lekovi.Visible := False;
Termini.Visible := False;
CallCentar_Grid.Visible := True;
Vakcine.Visible := False;
end;

end.
