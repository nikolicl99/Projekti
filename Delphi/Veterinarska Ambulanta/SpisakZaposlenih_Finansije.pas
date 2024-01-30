unit SpisakZaposlenih_Finansije;

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
  TfrmZaposleniFinansije = class(TForm)
    Nazad: TButton;
    Spisak: TStringGrid;
    ID: TIntegerColumn;
    Ime: TStringColumn;
    Prezime: TStringColumn;
    Tip: TStringColumn;
    Email: TStringColumn;
    Telefon: TStringColumn;
    Plate: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    procedure NazadClick(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure PlateClick(Sender: TObject);
    procedure SpisakCellClick(const Column: TColumn; const Row: Integer);
  private
    { Private declarations }
  public
    { Public declarations }
    SelectedID: integer;
  end;

var
  frmZaposleniFinansije: TfrmZaposleniFinansije;

implementation

{$R *.fmx}
uses Finansije_GlavnaForma, Main, PlateZaposlenih;

procedure TfrmZaposleniFinansije.FormCreate(Sender: TObject);
var
  Query: TFDQuery;
  Row: Integer;
begin
  Query := TFDQuery.Create(nil);
  try
    Query.Connection := GlobalConnection;
    Query.SQL.Text :=
      'SELECT zaposleni.idzaposlenog, zaposleni.ime, zaposleni.prezime, tip_zaposlenog.naziv_uloge, zaposleni.email, zaposleni.telefon ' +
  'FROM Zaposleni ' +
  'JOIN tip_zaposlenog ON zaposleni.funkcija = tip_zaposlenog.iduloge';
    Query.Open;

    Spisak.RowCount := Query.RecordCount;

    Row := 0;
    while not Query.Eof do
    begin
      Spisak.Cells[0, Row] := Query.FieldByName('idzaposlenog').AsString;
      Spisak.Cells[1, Row] := Query.FieldByName('ime').AsString;
      Spisak.Cells[2, Row] := Query.FieldByName('prezime').AsString;
      Spisak.Cells[3, Row] := Query.FieldByName('naziv_uloge').AsString;
      Spisak.Cells[4, Row] := Query.FieldByName('email').AsString;
      Spisak.Cells[5, Row] := Query.FieldByName('telefon').AsString;
      Query.Next;
      Inc(Row);
    end;

  finally
    Query.Free;
  end;
end;

procedure TfrmZaposleniFinansije.NazadClick(Sender: TObject);
begin
 frmfinansijemeni.Show;
 self.Hide;
end;
 procedure TfrmZaposleniFinansije.PlateClick(Sender: TObject);
begin
   frmPlateFinansije.Show;
   self.Hide;
end;

procedure TfrmZaposleniFinansije.SpisakCellClick(const Column: TColumn;
  const Row: Integer);
begin
SelectedID:= Spisak.Cells[0, Row].ToInteger;
end;

end.
