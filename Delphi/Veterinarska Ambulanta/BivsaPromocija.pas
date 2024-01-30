unit BivsaPromocija;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.Objects,
  FMX.StdCtrls, FMX.Controls.Presentation, System.Rtti, FMX.Grid.Style,
  FMX.Grid, FMX.ScrollBox, FMX.Edit, FireDAC.Stan.Intf, FireDAC.Stan.Option,
  FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf,
  FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt, Data.DB,
  FireDAC.Comp.DataSet, FireDAC.Comp.Client;

type
  TfrmBivsaPromocija = class(TForm)
    Forma: TPanel;
    Nazad: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Opis: TEdit;
    Tip: TEdit;
    Datum: TEdit;
    Datum_Label: TLabel;
    Tip_Label: TLabel;
    Opis_Label: TLabel;
    Korisnici: TStringGrid;
    Ime_Korisnik: TStringColumn;
    Prezime_Korisnici: TStringColumn;
    Zaposleni: TStringGrid;
    Ime_Zaposleni: TStringColumn;
    Prezime_Zaposleni: TStringColumn;
    Korisnici_Label: TLabel;
    Zaposleni_Label: TLabel;
    procedure FormCreate(Sender: TObject);
    procedure NazadClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmBivsaPromocija: TfrmBivsaPromocija;

implementation

{$R *.fmx}
uses SpisakPromocija, main;

procedure TfrmBivsaPromocija.FormCreate(Sender: TObject);
var
MyQuery: TFDQuery;
Query: TFDQuery;
Row: integer;
Red: integer;
begin
Datum.Text:= frmPromocije.SelectedDate;
Tip.Text:= frmPromocije.SelectedTip;
Opis.Text:= frmPromocije.SelectedOpis;
MyQuery:= TFDQuery.Create(nil);
try
  MyQuery.Connection := GlobalConnection;
  MyQuery.SQL.Text :=
  'SELECT Vlasnici.ImeVlasnika, Vlasnici.PrezimeVlasnika, Promocije.IDPromocije, Klijenti_Promocije.IDPromocije, Klijenti_Promocije.IDKlijenta '+
  'FROM Vlasnici '+
  'JOIN Klijenti_Promocije ON Vlasnici.IDVlasnika = Klijenti_Promocije.IDKlijenta '+
  'JOIN Promocije ON Klijenti_Promocije.IDPromocije = Promocije.IDPromocije '+
  'WHERE Promocije.IDPromocije = :PromocijaID';

  MyQuery.ParamByName('PromocijaID').AsInteger:= frmPromocije.SelectedID;
  MyQuery.Open;

  Korisnici.RowCount := MyQuery.RecordCount;

  Row := 0;
    while not MyQuery.Eof do
    begin
      Korisnici.Cells[0, Row] := MyQuery.FieldByName('ImeVlasnika').AsString;
      Korisnici.Cells[1, Row] := MyQuery.FieldByName('PrezimeVlasnika').AsString;
      MyQuery.Next;
      Inc(Row);
    end;
finally
  MyQuery.free;
end;

Query:= TFDQuery.Create(nil);
try
    Query.Connection := GlobalConnection;
  Query.SQL.Text :=
  'SELECT Zaposleni.Ime, Zaposleni.Prezime, Promocije.IDPromocije, Zaposleni_Promocije.IDPromocije, Zaposleni_Promocije.IDZaposlenog '+
  'FROM Zaposleni '+
  'JOIN Zaposleni_Promocije ON Zaposleni.IDZaposlenog = Zaposleni_Promocije.IDZaposlenog '+
  'JOIN Promocije ON Zaposleni_Promocije.IDPromocije = Promocije.IDPromocije '+
  'WHERE Promocije.IDPromocije = :PromocijaID';

  Query.ParamByName('PromocijaID').AsInteger:= frmPromocije.SelectedID;
  Query.Open;

  Zaposleni.RowCount := Query.RecordCount;

  Red := 0;
    while not Query.Eof do
    begin
      Zaposleni.Cells[0, Red] := Query.FieldByName('Ime').AsString;
      Zaposleni.Cells[1, Red] := Query.FieldByName('Prezime').AsString;
      Query.Next;
      Inc(Red);
    end;
finally
      Query.free;
end;
end;

procedure TfrmBivsaPromocija.NazadClick(Sender: TObject);
begin
frmPromocije.show;
self.hide;
end;

end.
