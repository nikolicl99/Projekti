unit Transakcije;

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
  TfrmTransakcije = class(TForm)
    Forma: TPanel;
    Dostave: TButton;
    Nazad: TButton;
    Spisak: TStringGrid;
    ID: TStringColumn;
    Ime: TStringColumn;
    Prezime: TStringColumn;
    Telefon: TStringColumn;
    Adresa: TStringColumn;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Ziro: TStringColumn;
    procedure NazadClick(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure UkloniClick(Sender: TObject);
    procedure SpisakCellClick(const Column: TColumn; const Row: Integer);
    procedure DostaveClick(Sender: TObject);
  private
    { Private declarations }

  public
    { Public declarations }
    SelectedID: integer;
  end;

var
  frmTransakcije: TfrmTransakcije;

implementation
uses Main, SpisakZaposlenih, Finansije_Dostava, Finansije_GlavnaForma;

{$R *.fmx}

procedure TfrmTransakcije.NazadClick(Sender: TObject);
begin
frmfinansijemeni.Show;
 self.Hide;
end;

 procedure TfrmTransakcije.SpisakCellClick(const Column: TColumn;
  const Row: Integer);
begin
SelectedID:= Spisak.Cells[0, Row].ToInteger;
end;


procedure TfrmTransakcije.UkloniClick(Sender: TObject);
var
  MyQuery: TFDQuery;
begin
MyQuery := TFDQuery.Create(nil);
try
MyQuery.Connection:= GlobalConnection;
MyQuery.SQL.Text := 'DELETE FROM Transakcije WHERE idtransakcije = :transakcijaid';
    MyQuery.ParamByName('transakcijaid').AsInteger := SelectedID;
    MyQuery.ExecSQL;
finally
  MyQuery.free;
  showmessage('Transakcija uklonjena');
  frmtransakcije.Activate;
end;
end;


procedure TfrmTransakcije.DostaveClick(Sender: TObject);
begin
frmFinansijeDostava.show;
self.hide;
end;

procedure TfrmTransakcije.FormCreate(Sender: TObject);
var
  Query: TFDQuery;
  Row: Integer;
begin
    Query := TFDQuery.Create(nil);
    try
        Query.Connection := GlobalConnection;


    Query.SQL.Text :=
      'SELECT   Dobavljaci.IDDobavljaca, Dobavljaci.Ziro_Racun, Dobavljaci.Ime, Dobavljaci.Prezime, Dobavljaci.Telefon, Adresa_Stanovanja.Adresa'
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
      Spisak.Cells[3, Row] := Query.FieldByName('Ziro_Racun').AsString;
      Spisak.Cells[4, Row] := Query.FieldByName('Telefon').AsString;
      Spisak.Cells[5, Row] := Query.FieldByName('Adresa').AsString;

      Query.Next;
      Inc(Row);
    end;

    finally
       Query.Free;
    end;

end;

end.
