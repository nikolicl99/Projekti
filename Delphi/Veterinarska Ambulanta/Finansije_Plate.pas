unit Finansije_Plate;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.ListBox,
  FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Param,
  FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf, FireDAC.DApt.Intf,
  FireDAC.Stan.Async, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client, Datasnap.Provider, FMX.Controls.Presentation,
  FMX.StdCtrls, Datasnap.DBClient, FMX.Edit, System.Rtti, FMX.Grid.Style,
  FMX.ScrollBox, FMX.Grid, FMX.Objects;

type
  TfrmFinansijePlate = class(TForm)
    Forma: TPanel;
    Nazad: TButton;
    Spisak: TStringGrid;
    ID: TIntegerColumn;
    Datum_Od: TStringColumn;
    Datum_Do: TStringColumn;
    Plata_sat: TIntegerColumn;
    broj_sat: TIntegerColumn;
    Plata_Ukupno: TIntegerColumn;
    Valuta: TStringColumn;
    Aktivnost: TStringColumn;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Zaposleni: TComboBox;
    Zaposleni_Label: TLabel;
    Zaposleni_col: TStringColumn;
    procedure FormActivate(Sender: TObject);
    procedure ZaposleniChange(Sender: TObject);
    function GetIDZaposlenog(const imeZaposlenog: string): Integer;
    procedure NazadClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmFinansijePlate: TfrmFinansijePlate;

implementation

{$R *.fmx}
uses main, SpisakZaposlenih_Finansije, Finansije_GlavnaForma;

procedure TfrmFinansijePlate.FormActivate(Sender: TObject);
  var
  MyQuery: TFDQuery;
  Query: TFDQuery;
  Row: Integer;
begin
    Query := TFDQuery.Create(nil);
    try
      Query.Connection := GlobalConnection;
      Query.SQL.Text := 'SELECT Plate_Zaposlenih.*, Zaposleni.*, Aktivnost.* FROM Plate_Zaposlenih ' +
      'JOIN Aktivnost ON Plate_Zaposlenih.Aktivna = Aktivnost.IDAktivnosti '+
      'JOIN Zaposleni ON Plate_Zaposlenih.IDZaposlenog = Zaposleni.IDZaposlenog '+
      'WHERE Aktivna = 3 '+
      'ORDER BY julianday(substr(Plate_Zaposlenih.Datum_Od, 7, 4) || "-" || substr(Plate_Zaposlenih.Datum_Od, 4, 2) || "-" || substr(Plate_Zaposlenih.Datum_Od, 1, 2)) DESC';

      Query.Open;
      Spisak.RowCount := Query.RecordCount;

      Row := 0;
      while not Query.Eof do
    begin
      Spisak.Cells[0, Row] := Query.FieldByName('IDUplate').AsString;
      Spisak.Cells[1, Row] := Query.FieldByName('ime').AsString + ' ' + Query.FieldByName('prezime').AsString;
      Spisak.Cells[2, Row] := Query.FieldByName('Datum_Od').AsString;
      Spisak.Cells[3, Row] := Query.FieldByName('Datum_Do').AsString;
      Spisak.Cells[4, Row] := Query.FieldByName('Plata_po_Satu').AsString;
      Spisak.Cells[5, Row] := Query.FieldByName('Broj_Radnih_Sati').AsString;
      Spisak.Cells[6, Row] := Query.FieldByName('Ukupno_Za_Placanje').AsString;
      Spisak.Cells[7, Row] := Query.FieldByName('Valuta').AsString;
      Spisak.Cells[8, Row] := Query.FieldByName('Status').AsString;
      Query.Next;
      Inc(Row);
    end;
    finally
        Query.Free;
    end;
    MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Clear;
    MyQuery.SQL.Text := 'SELECT * FROM Zaposleni';
    MyQuery.Open;

    Zaposleni.Clear;

    while not MyQuery.Eof do
    begin
      Zaposleni.Items.Add(MyQuery.FieldbyName('Ime').AsString + ' ' + MyQuery.FieldByName('Prezime').AsString);
      MyQuery.Next;
    end;
  finally
    MyQuery.Free;
    MyQuery.Close;
  end;
end;

procedure TfrmFinansijePlate.ZaposleniChange(Sender: TObject);
var
  Query: TFDQuery;
  Row: Integer;
begin
    Query := TFDQuery.Create(nil);
    try
      Query.Connection := GlobalConnection;
      Query.SQL.Text := 'SELECT Plate_Zaposlenih.*, Zaposleni.*, Aktivnost.* FROM Plate_Zaposlenih ' +
      'JOIN Aktivnost ON Plate_Zaposlenih.Aktivna = Aktivnost.IDAktivnosti '+
      'JOIN Zaposleni ON Plate_Zaposlenih.IDZaposlenog = Zaposleni.IDZaposlenog '+
      'WHERE Plate_Zaposlenih.IDZaposlenog = :ZaposleniID AND Aktivna = 3 '+
      'ORDER BY julianday(substr(Plate_Zaposlenih.Datum_Od, 7, 4) || "-" || substr(Plate_Zaposlenih.Datum_Od, 4, 2) || "-" || substr(Plate_Zaposlenih.Datum_Od, 1, 2)) DESC';

      Query.ParamByName('ZaposleniID').AsInteger:= getidzaposlenog(zaposleni.Selected.Text);
      Query.Open;
      Spisak.RowCount := Query.RecordCount;

      Row := 0;
      while not Query.Eof do
    begin
      Spisak.Cells[0, Row] := Query.FieldByName('IDUplate').AsString;
      Spisak.Cells[1, Row] := Query.FieldByName('ime').AsString + ' ' + Query.FieldByName('prezime').AsString;
      Spisak.Cells[2, Row] := Query.FieldByName('Datum_Od').AsString;
      Spisak.Cells[3, Row] := Query.FieldByName('Datum_Do').AsString;
      Spisak.Cells[4, Row] := Query.FieldByName('Plata_po_Satu').AsString;
      Spisak.Cells[5, Row] := Query.FieldByName('Broj_Radnih_Sati').AsString;
      Spisak.Cells[6, Row] := Query.FieldByName('Ukupno_Za_Placanje').AsString;
      Spisak.Cells[7, Row] := Query.FieldByName('Valuta').AsString;
      Spisak.Cells[8, Row] := Query.FieldByName('Status').AsString;
      Query.Next;
      Inc(Row);
    end;
    finally
        Query.Free;
    end;

end;

function TfrmFinansijePlate.GetIDZaposlenog(const imeZaposlenog: string): Integer;
var
  MyQuery: TFDQuery;
  Ime, Prezime: string;
begin
  Result := -1;
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT idzaposlenog, Ime, Prezime FROM zaposleni';
    MyQuery.Open;

    while not MyQuery.Eof do
    begin
      Ime := MyQuery.FieldByName('Ime').AsString;
      Prezime := MyQuery.FieldByName('Prezime').AsString;

      if SameText(imeZaposlenog, Ime + ' ' + Prezime) then
      begin
        Result := MyQuery.FieldByName('idzaposlenog').AsInteger;
      end;

      MyQuery.Next;
    end;
  finally
    MyQuery.Free;
  end;
end;
procedure TfrmFinansijePlate.NazadClick(Sender: TObject);
begin
frmfinansijemeni.show;
self.hide;
end;

end.
