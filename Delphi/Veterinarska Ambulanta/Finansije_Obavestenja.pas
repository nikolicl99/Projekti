unit Finansije_Obavestenja;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, System.Rtti,
  FMX.Grid.Style, FMX.Objects, FMX.StdCtrls, FMX.Grid, FMX.ScrollBox,
  FMX.Controls.Presentation, FireDAC.Stan.Intf, FireDAC.Stan.Option,
  FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf,
  FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt, Data.DB,
  FireDAC.Comp.DataSet, FireDAC.Comp.Client;

type
  TfrmfinansijeObavestenja = class(TForm)
    Forma: TPanel;
    Plate: TStringGrid;
    Transakcije: TStringGrid;
    Nazad: TButton;
    Plate_but: TRadioButton;
    Transakcije_but: TRadioButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Dobavljac: TStringColumn;
    Datum: TStringColumn;
    Kolicina: TStringColumn;
    Iznos: TStringColumn;
    Valuta: TStringColumn;
    Status: TStringColumn;
    Zaposleni: TStringColumn;
    DatumOd: TStringColumn;
    DatumDo: TStringColumn;
    Plata_sat: TStringColumn;
    Broj_Sati: TStringColumn;
    Ukupno: TStringColumn;
    Aktivna: TStringColumn;
    Valuta_pl: TStringColumn;
    procedure FormCreate(Sender: TObject);
    procedure Transakcije_butChange(Sender: TObject);
    procedure Plate_butChange(Sender: TObject);
    procedure NazadClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmfinansijeObavestenja: TfrmfinansijeObavestenja;

implementation

{$R *.fmx}
uses main, finansije_Glavnaforma;

procedure TfrmfinansijeObavestenja.FormCreate(Sender: TObject);
var
  Query: TFDQuery;
  MyQuery: TFDQuery;
  Row: integer;
begin
  Query := TFDQuery.Create(nil);
  try
    Query.Connection := GlobalConnection;
     Query.SQL.Text :=
      'SELECT Nabavka.*, Dobavljaci.* ' +
      'FROM Nabavka ' +
      'JOIN Dobavljaci ON Nabavka.IDDobavljaca = Dobavljaci.IDDobavljaca '+
      'WHERE Nabavka.Status = ''ON HOLD'' '+
      'ORDER BY julianday(substr(Nabavka.Datum_Nabavke, 7, 4) || "-" || substr(Nabavka.Datum_Nabavke, 4, 2) || "-" || substr(Nabavka.Datum_Nabavke, 1, 2)) DESC';
    Query.Open;

    Transakcije.RowCount := Query.RecordCount;

    Row:= 0;
    while not Query.Eof do
    begin
          Transakcije.Cells[0, Row] := Query.FieldByName('Ime').AsString + ' ' + Query.FieldByName('Prezime').AsString;
          Transakcije.Cells[1, Row] := Query.FieldByName('Datum_Nabavke').AsString;
          Transakcije.Cells[2, Row] := Query.FieldByName('Kolicina').AsString;
          Transakcije.Cells[3, Row] := Query.FieldByName('Iznos').AsString;
          Transakcije.Cells[4, Row] := Query.FieldByName('Valuta').AsString;
          Transakcije.Cells[5, Row] := Query.FieldByName('Status').AsString;
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
      'SELECT Plate_Zaposlenih.*, Zaposleni.*, Aktivnost.* ' +
      'FROM Plate_Zaposlenih ' +
      'JOIN Zaposleni ON Plate_Zaposlenih.IDZaposlenog = Zaposleni.IDZaposlenog '+
      'JOIN Aktivnost ON Plate_Zaposlenih.Aktivna = Aktivnost.IDAktivnosti '+
      'WHERE Plate_Zaposlenih.Aktivna = 2 ';
      MyQuery.Open;

    Plate.RowCount := MyQuery.RecordCount;

    Row:= 0;
    while not MyQuery.Eof do
    begin
          Plate.Cells[0, Row] := MyQuery.FieldByName('Ime').AsString + ' ' + Query.FieldByName('Prezime').AsString;
          Plate.Cells[1, Row] := MyQuery.FieldByName('Datum_Od').AsString;
          Plate.Cells[2, Row] := MyQuery.FieldByName('Datum_Do').AsString;
          Plate.Cells[3, Row] := MyQuery.FieldByName('Plata_po_Satu').AsString;
          Plate.Cells[4, Row] := MyQuery.FieldByName('Broj_Radnih_Sati').AsString;
          Plate.Cells[5, Row] := MyQuery.FieldByName('Ukupno_Za_Placanje').AsString;
          Plate.Cells[6, Row] := MyQuery.FieldByName('Valuta').AsString;
          Plate.Cells[7, Row] := MyQuery.FieldByName('Status').AsString;
          MyQuery.Next;
          Inc(Row);
        end;

 finally
    Query.Free;
end;

end;

procedure TfrmfinansijeObavestenja.NazadClick(Sender: TObject);
begin
frmfinansijemeni.show;
self.hide;
end;

procedure TfrmfinansijeObavestenja.Plate_butChange(Sender: TObject);
begin
transakcije.Visible := False;
Plate.Visible := True;
end;

procedure TfrmfinansijeObavestenja.Transakcije_butChange(Sender: TObject);
begin
transakcije.Visible := True;
Plate.Visible := False;
end;

end.
