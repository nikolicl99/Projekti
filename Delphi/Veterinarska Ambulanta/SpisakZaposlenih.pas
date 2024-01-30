unit SpisakZaposlenih;

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
  TfrmZaposleni = class(TForm)
    Nazad: TButton;
    Spisak: TStringGrid;
    ID: TIntegerColumn;
    Ime: TStringColumn;
    Prezime: TStringColumn;
    Tip: TStringColumn;
    Email: TStringColumn;
    Telefon: TStringColumn;
    Dodaj: TButton;
    Ukloni: TButton;
    Plate: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    JMBG: TIntegerColumn;
    procedure NazadClick(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure DodajClick(Sender: TObject);
    procedure UkloniClick(Sender: TObject);
    procedure PlateClick(Sender: TObject);
    procedure SpisakCellClick(const Column: TColumn; const Row: Integer);
  private
    { Private declarations }

  public
    { Public declarations }
    SelectedID: integer;
  end;

var
  frmZaposleni: TfrmZaposleni;

implementation

{$R *.fmx}
uses LoginZaposleni, Main, NoviZaposleni, SpisakPlata;

procedure TfrmZaposleni.DodajClick(Sender: TObject);
begin
frmNoviZaposleni.Show;
self.Hide;
end;

procedure TfrmZaposleni.FormCreate(Sender: TObject);
var
  Query: TFDQuery;
  Row: Integer;
begin
  Query := TFDQuery.Create(nil);
  try
    Query.Connection := GlobalConnection;
    Query.SQL.Text :=
      'SELECT zaposleni.idzaposlenog, zaposleni.jmbg, zaposleni.ime, zaposleni.prezime, tip_zaposlenog.naziv_uloge, zaposleni.email, zaposleni.telefon ' +
  'FROM Zaposleni ' +
  'JOIN tip_zaposlenog ON zaposleni.funkcija = tip_zaposlenog.iduloge ';
    Query.Open;

    Spisak.RowCount := Query.RecordCount;

    Row := 0;
    while not Query.Eof do
    begin
      Spisak.Cells[0, Row] := Query.FieldByName('idzaposlenog').AsString;
      Spisak.Cells[1, Row] := Query.FieldByName('ime').AsString;
      Spisak.Cells[2, Row] := Query.FieldByName('prezime').AsString;
      Spisak.Cells[3, Row] := Query.FieldByName('jmbg').AsString;
      Spisak.Cells[4, Row] := Query.FieldByName('naziv_uloge').AsString;
      Spisak.Cells[5, Row] := Query.FieldByName('email').AsString;
      Spisak.Cells[6, Row] := Query.FieldByName('telefon').AsString;
      Query.Next;
      Inc(Row);
    end;

  finally
    Query.Free;
  end;
end;

procedure TfrmZaposleni.NazadClick(Sender: TObject);
begin
 frmLogInZaposleni.Show;
 self.Hide;
end;

procedure TfrmZaposleni.PlateClick(Sender: TObject);
begin
frmPlate.show;
self.Hide;
end;

procedure TfrmZaposleni.SpisakCellClick(const Column: TColumn;
  const Row: Integer);
begin
SelectedID:= Spisak.Cells[0, Row].ToInteger;
end;

procedure TfrmZaposleni.UkloniClick(Sender: TObject);
begin
var
  Query: TFDQuery;
  begin
    Query := TFDQuery.Create(nil);
    try
      Query.Connection:= GlobalConnection;
      Query.SQL.Text := 'DELETE FROM Zaposleni WHERE IDZaposlenog = :zaposleniID';
      Query.ParamByName('zaposleniID').AsInteger := SelectedID;
      Query.ExecSQL;
    finally
        Query.free;
        showmessage('Uklonili ste zaposlenog!');
        frmZaposleni.Activate;
    end;
  end;
end;

end.
