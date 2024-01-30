unit PlateZaposlenih;

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
  TfrmPlateFinansije = class(TForm)
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
    Potvrdi: TButton;
    procedure NazadClick(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure SpisakCellClick(const Column: TColumn; const Row: Integer);
    procedure PotvrdiClick(Sender: TObject);
  private
    { Private declarations }
    SelektovanID: integer;
  public
    { Public declarations }
  end;

var
  frmPlateFinansije: TfrmPlateFinansije;

implementation
uses Main, SpisakZaposlenih_Finansije;

{$R *.fmx}

procedure TfrmPlateFinansije.FormCreate(Sender: TObject);
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
       Query.ParamByName('ZaposleniID').AsInteger := frmZaposleniFinansije.SelectedID;
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

procedure TfrmPlateFinansije.NazadClick(Sender: TObject);
begin
 frmZaposleniFinansije.Show;
 self.Hide;
end;

procedure TfrmPlateFinansije.PotvrdiClick(Sender: TObject);
var
  MyQuery: TFDQuery;
begin
MyQuery := TFDQuery.Create(nil);
try
MyQuery.Connection:= GlobalConnection;
MyQuery.SQL.Text := 'UPDATE Plate_Zaposlenih SET Aktivna = ''1'' WHERE iduplate = :uplataid';
    MyQuery.ParamByName('uplataid').AsInteger := SelektovanID;
    MyQuery.ExecSQL;
finally
  MyQuery.free;
  showmessage('Uplata potvrdjena');
  frmplatefinansije.Activate;
end;
end;

procedure TfrmPlateFinansije.SpisakCellClick(const Column: TColumn;
  const Row: Integer);
begin
SelektovanID:= Spisak.Cells[0, Row].ToInteger;
end;

end.
