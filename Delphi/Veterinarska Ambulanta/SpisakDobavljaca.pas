unit SpisakDobavljaca;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs,
  FMX.Controls.Presentation, FMX.StdCtrls, System.Rtti, FMX.Grid.Style,
  FMX.Grid, FMX.ScrollBox, FireDAC.Stan.Intf, FireDAC.Stan.Option,
  FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf,
  FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt, Data.DB,
  FireDAC.Comp.DataSet, FireDAC.Comp.Client, FMX.Objects;

type
  TfrmDobavljaci = class(TForm)
    Nazad: TButton;
    Spisak: TStringGrid;
    ID: TStringColumn;
    Ime: TStringColumn;
    Prezime: TStringColumn;
    Telefon: TStringColumn;
    Adresa: TStringColumn;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    Dostave: TButton;
    procedure NazadClick(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure DostaveClick(Sender: TObject);
    procedure SpisakCellClick(const Column: TColumn; const Row: Integer);
  private
    { Private declarations }
  public
    { Public declarations }
    DobavljacID: integer;
  end;

var
  frmDobavljaci: TfrmDobavljaci;

implementation

{$R *.fmx}

uses Main, Inventar, Dostave;

procedure TfrmDobavljaci.DostaveClick(Sender: TObject);
begin
frmDostave.show;
self.hide;
end;

procedure TfrmDobavljaci.FormCreate(Sender: TObject);
var
  Query: TFDQuery;
  Row: Integer;
begin
    Query := TFDQuery.Create(nil);
    try
        Query.Connection := GlobalConnection;


    Query.SQL.Text :=
      'SELECT   Dobavljaci.IDDobavljaca, Dobavljaci.Ime, Dobavljaci.Prezime, Dobavljaci.Telefon, Adresa_Stanovanja.Adresa'
            +' FROM Dobavljaci '
            + 'JOIN Adresa_Stanovanja ON Dobavljaci.Adresa_Stanovanja = Adresa_Stanovanja.IDAdrese';

       Query.Open;
    Spisak.RowCount := Query.RecordCount;

       Row := 0;
    while not Query.Eof do
    begin
      Spisak.Cells[0, Row] := Query.FieldByName('IDDobavljaca').AsString;
      Spisak.Cells[1, Row] := Query.FieldByName('Ime').AsString;
      Spisak.Cells[2, Row] := Query.FieldByName('Prezime').AsString;
      Spisak.Cells[3, Row] := Query.FieldByName('Telefon').AsString;
      Spisak.Cells[4, Row] := Query.FieldByName('Adresa').AsString;

      Query.Next;
      Inc(Row);
    end;

    finally
       Query.Free;
    end;

end;

procedure TfrmDobavljaci.NazadClick(Sender: TObject);
begin
    frmInventar.Show;
    self.Hide;
end;

procedure TfrmDobavljaci.SpisakCellClick(const Column: TColumn;
  const Row: Integer);
begin
DobavljacID := Spisak.Cells[0, Row].ToInteger;
end;

end.
