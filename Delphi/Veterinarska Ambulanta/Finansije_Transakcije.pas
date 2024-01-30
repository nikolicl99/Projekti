unit Finansije_Transakcije;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, System.Rtti,
  FMX.Grid.Style, FMX.Grid, FMX.Controls.Presentation, FMX.ScrollBox,
  FMX.StdCtrls, FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Param,
  FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf, FireDAC.DApt.Intf,
  FireDAC.Stan.Async, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client, FMX.Objects, FMX.ListBox;

type
  TfrmfinansijeTransakcije = class(TForm)
    Forma: TPanel;
    Spisak: TStringGrid;
    IDNabavke: TStringColumn;
    Tip: TStringColumn;
    Naziv: TStringColumn;
    Dobavljac: TStringColumn;
    Datum: TDateColumn;
    Kolicina: TStringColumn;
    Nazad: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Proizvod: TComboBox;
    Proizvod_Label: TLabel;
    procedure FormActivate(Sender: TObject);
    procedure ProizvodChange(Sender: TObject);
    procedure NazadClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmfinansijeTransakcije: TfrmfinansijeTransakcije;

implementation

{$R *.fmx}
uses main, finansije_GlavnaForma;

procedure TfrmfinansijeTransakcije.FormActivate(Sender: TObject);
var
  MyQuery: TFDQuery;
  Query: TFDQuery;
  Row: Integer;
begin
      MyQuery := TFDQuery.Create(nil);
      try
        MyQuery.Connection := GlobalConnection;
        MyQuery.SQL.Text := 'SELECT * FROM Inventar';
        MyQuery.Open;
        Proizvod.Clear;
    while not MyQuery.Eof do
    begin
      Proizvod.Items.Add(MyQuery.FieldByName('Ime').AsString);
      MyQuery.Next;
    end;
      finally
      MyQuery.Free;
      MyQuery.Close;
      end;
     Query := TFDQuery.Create(nil);
     try
        Query.Connection := GlobalConnection;
        Query.SQL.Text := 'SELECT Nabavka.*, Inventar.ime, Inventar.id, Inventar.tip, Dobavljaci.Ime || " " || Dobavljaci.Prezime AS ImePrezime, Dobavljaci.IDDobavljaca '+
        'FROM Nabavka JOIN Dobavljaci ON Nabavka.IDDobavljaca = Dobavljaci.IDDobavljaca '+
        'JOIN Inventar ON Nabavka.IDProizvoda = Inventar.id WHERE Status = ''COMPLETED'' '+
        'ORDER BY julianday(substr(Nabavka.Datum_Nabavke, 7, 4) || "-" || substr(Nabavka.Datum_Nabavke, 4, 2) || "-" || substr(Nabavka.Datum_Nabavke, 1, 2)) DESC';
        Query.Open;

     Spisak.RowCount := Query.RecordCount;

    Row := 0;
    while not Query.Eof do
    begin
      Spisak.Cells[0, Row] := Query.FieldByName('IDNabavke').AsString;
      Spisak.Cells[1, Row] := Query.FieldByName('Tip').AsString;
      Spisak.Cells[2, Row] := Query.FieldByName('Ime').AsString;
      Spisak.Cells[3, Row] := Query.FieldByName('ImePrezime').AsString;
      Spisak.Cells[4, Row] := Query.FieldByName('Datum_Nabavke').AsString;
      Spisak.Cells[5, Row] := Query.FieldByName('Kolicina').AsString;

      Query.Next;
      Inc(Row);
    end;
  finally
    Query.Free;
  end;
end;
procedure TfrmfinansijeTransakcije.NazadClick(Sender: TObject);
begin

frmfinansijemeni.show;
self.hide;

end;

procedure TfrmfinansijeTransakcije.ProizvodChange(Sender: TObject);
var
  Query: TFDQuery;
  Row: integer;
begin
Query := TFDQuery.Create(nil);
     try
        Query.Connection := GlobalConnection;
        Query.SQL.Text := 'SELECT Nabavka.*, Inventar.ime, Inventar.id, Inventar.tip, Dobavljaci.Ime || " " || Dobavljaci.Prezime AS ImePrezime, Dobavljaci.IDDobavljaca '+
        'FROM Nabavka JOIN Dobavljaci ON Nabavka.IDDobavljaca = Dobavljaci.IDDobavljaca '+
        'JOIN Inventar ON Nabavka.IDProizvoda = Inventar.id WHERE Status = ''COMPLETED'' '+
        'AND Inventar.Ime = :Proizvod '+
        'ORDER BY julianday(substr(Nabavka.Datum_Nabavke, 7, 4) || "-" || substr(Nabavka.Datum_Nabavke, 4, 2) || "-" || substr(Nabavka.Datum_Nabavke, 1, 2)) DESC';
        Query.ParamByName('Proizvod').AsString := Proizvod.Selected.Text;
        Query.Open;

     Spisak.RowCount := Query.RecordCount;

    Row := 0;
    while not Query.Eof do
    begin
      Spisak.Cells[0, Row] := Query.FieldByName('IDNabavke').AsString;
      Spisak.Cells[1, Row] := Query.FieldByName('Tip').AsString;
      Spisak.Cells[2, Row] := Query.FieldByName('Ime').AsString;
      Spisak.Cells[3, Row] := Query.FieldByName('ImePrezime').AsString;
      Spisak.Cells[4, Row] := Query.FieldByName('Datum_Nabavke').AsString;
      Spisak.Cells[5, Row] := Query.FieldByName('Kolicina').AsString;

      Query.Next;
      Inc(Row);
    end;
  finally
    Query.Free;
  end;
end;

end.
