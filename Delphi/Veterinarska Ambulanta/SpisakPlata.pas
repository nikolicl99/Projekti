unit SpisakPlata;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, System.Rtti,
  FMX.Grid.Style, FMX.Grid, FMX.ScrollBox, FMX.Controls.Presentation,
  FMX.StdCtrls, FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Param,
  FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf, FireDAC.DApt.Intf,
  FireDAC.Stan.Async, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client, FMX.Objects;

type
  TfrmPlate = class(TForm)
    Nazad: TButton;
    Spisak: TStringGrid;
    Valuta: TStringColumn;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    Datum_Od: TStringColumn;
    Datum_Do: TStringColumn;
    Plata_sat: TIntegerColumn;
    broj_sat: TIntegerColumn;
    Plata_Ukupno: TIntegerColumn;
    Dodaj: TButton;
    Aktivnost: TStringColumn;
    Ukloni: TButton;
    ID: TIntegerColumn;
    procedure FormCreate(Sender: TObject);
    procedure NazadClick(Sender: TObject);
    procedure DodajClick(Sender: TObject);
    procedure UkloniClick(Sender: TObject);
    procedure SpisakCellClick(const Column: TColumn; const Row: Integer);
  private
    { Private declarations }
    SelektovanID: integer;
  public
    { Public declarations }
  end;

var
  frmPlate: TfrmPlate;

implementation

{$R *.fmx}

uses Main, SpisakZaposlenih, UplataPlata;

procedure TfrmPlate.DodajClick(Sender: TObject);
begin
frmDodajUplatu.Show;
self.Hide;
end;

procedure TfrmPlate.FormCreate(Sender: TObject);
  var
  Query: TFDQuery;
  Row: Integer;
begin
    Query := TFDQuery.Create(nil);
    try
      Query.Connection := GlobalConnection;
      Query.SQL.Text := 'SELECT Plate_Zaposlenih.*, Aktivnost.* FROM Plate_Zaposlenih ' +
      'JOIN Aktivnost ON Plate_Zaposlenih.Aktivna = Aktivnost.IDAktivnosti '+
      'WHERE IDZaposlenog = :ZaposleniID';
       Query.ParamByName('ZaposleniID').AsInteger := frmZaposleni.SelectedID;
      Query.Open;
      Spisak.RowCount := Query.RecordCount;

      Row := 0;
      while not Query.Eof do
    begin
      Spisak.Cells[0, Row] := Query.FieldByName('IDUplate').AsString;
      Spisak.Cells[1, Row] := Query.FieldByName('Datum_Od').AsString;
      Spisak.Cells[2, Row] := Query.FieldByName('Datum_Do').AsString;
      Spisak.Cells[3, Row] := Query.FieldByName('Plata_po_Satu').AsString;
      Spisak.Cells[4, Row] := Query.FieldByName('Broj_Radnih_Sati').AsString;
      Spisak.Cells[5, Row] := Query.FieldByName('Ukupno_Za_Placanje').AsString;
      Spisak.Cells[6, Row] := Query.FieldByName('Valuta').AsString;
      Spisak.Cells[7, Row] := Query.FieldByName('Status').AsString;
      Query.Next;
      Inc(Row);
    end;
    finally
        Query.Free;
    end;
end;

procedure TfrmPlate.NazadClick(Sender: TObject);
begin
frmZaposleni.show;
self.hide;
end;

procedure TfrmPlate.SpisakCellClick(const Column: TColumn; const Row: Integer);
begin
SelektovanID:= Spisak.Cells[0, Row].ToInteger;
end;

procedure TfrmPlate.UkloniClick(Sender: TObject);
var
  MyQuery: TFDQuery;
begin
MyQuery := TFDQuery.Create(nil);
try
MyQuery.Connection:= GlobalConnection;
MyQuery.SQL.Text := 'DELETE FROM Plate_Zaposlenih WHERE iduplate = :uplataid';
    MyQuery.ParamByName('uplataid').AsInteger := SelektovanID;
    MyQuery.ExecSQL;
finally
  MyQuery.free;
  showmessage('Uplata uklonjena');
  frmplate.Activate;
end;
end;

end.
