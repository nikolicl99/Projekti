unit PromocijaLista;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, System.Rtti,
  FMX.Grid.Style, FMX.Objects, FMX.StdCtrls, FMX.Grid, FMX.ScrollBox,
  FMX.Controls.Presentation, FireDAC.Stan.Intf, FireDAC.Stan.Option,
  FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf,
  FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt, Data.DB,
  FireDAC.Comp.DataSet, FireDAC.Comp.Client, FMX.ListBox;

type
  TfrmListaPromocija = class(TForm)
    Forma: TPanel;
    Zaposleni: TStringGrid;
    Klijenti: TStringGrid;
    Nazad: TButton;
    Zaposleni_but: TRadioButton;
    Klijenti_but: TRadioButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Tip_kl_col: TStringColumn;
    Datum_kl: TStringColumn;
    Opis_kl: TStringColumn;
    ID_kl_col: TIntegerColumn;
    Klijenti_cb: TComboBox;
    Klijenti_Label: TLabel;
    Zaposleni_cb: TComboBox;
    Zaposleni_Label: TLabel;
    id_zap_col: TIntegerColumn;
    Tip_zap: TStringColumn;
    Datum_zap: TStringColumn;
    Opis_zap: TStringColumn;
    procedure FormCreate(Sender: TObject);
    procedure Zaposleni_cbChange(Sender: TObject);
    function GetIDZaposlenog(const imeZaposlenog: string): Integer;
    procedure Klijenti_cbChange(Sender: TObject);
    function GetIDKlijenta(const imeKlijenta: string): Integer;
    procedure NazadClick(Sender: TObject);
    procedure Klijenti_butChange(Sender: TObject);
    procedure Zaposleni_butChange(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmListaPromocija: TfrmListaPromocija;

implementation

{$R *.fmx}
uses Main, SpisakPromocija;

procedure TfrmListaPromocija.FormCreate(Sender: TObject);
var
  MyQuery: TFDQuery;
  Query: TFDQuery;
begin
      MyQuery := TFDQuery.Create(nil);
      try
        MyQuery.Connection := GlobalConnection;
        MyQuery.SQL.Text := 'SELECT ImeVlasnika, PrezimeVlasnika FROM Vlasnici';
        MyQuery.Open;
        Klijenti_cb.Items.Clear;
    while not MyQuery.Eof do
    begin
      Klijenti_cb.Items.Add(MyQuery.FieldByName('ImeVlasnika').AsString + ' ' + MyQuery.FieldByName('PrezimeVlasnika').AsString);
      MyQuery.Next;
    end;

      finally
          MyQuery.free;
      end;
      Query := TFDQuery.Create(nil);
      try
        Query.Connection := GlobalConnection;
        Query.SQL.Text := 'SELECT Ime, Prezime FROM Zaposleni';
        Query.Open;
        Zaposleni_cb.Items.Clear;
    while not Query.Eof do
    begin
      Zaposleni_cb.Items.Add(Query.FieldByName('Ime').AsString + ' ' + Query.FieldByName('Prezime').AsString);
      Query.Next;
    end;

      finally
          Query.free;
      end;
end;

procedure TfrmListaPromocija.Zaposleni_butChange(Sender: TObject);
begin
Klijenti_Label.Visible := False;
Klijenti_cb.Visible := False;
Klijenti.Visible := False;
Zaposleni_Label.Visible := True;
Zaposleni_cb.Visible := True;
Zaposleni.Visible := True;
end;

procedure TfrmListaPromocija.Zaposleni_cbChange(Sender: TObject);
var
  Kveri: TFDQuery;
  Row: Integer;
begin
  Kveri := TFDQuery.Create(nil);
  try
    Kveri.Connection := GlobalConnection;
    Kveri.SQL.Text := 'SELECT Zaposleni_Promocije.IDZaposlenog, Promocije.IDPromocije, Vrste_Promocija.Naziv, Promocije.Datum, Promocije.Opis ' +
  'FROM Promocije ' +
  'JOIN Vrste_Promocija ON Promocije.Vrsta_Promocije = Vrste_Promocija.IDPromocije ' +
  'JOIN Zaposleni_Promocije ON Promocije.IDPromocije = Zaposleni_Promocije.IDPromocije '+
  'WHERE Zaposleni_Promocije.IDZaposlenog = :ZaposleniID '+
  'ORDER BY julianday(substr(Promocije.Datum, 7, 4) || "-" || substr(Promocije.Datum, 4, 2) || "-" || substr(Promocije.Datum, 1, 2)) DESC';
    Kveri.ParamByName('ZaposleniID').AsInteger := GetIDZaposlenog(Zaposleni_cb.Selected.Text);
    Kveri.Open;

    Zaposleni.RowCount := Kveri.RecordCount;

    Row := 0;
    while not Kveri.Eof do
    begin
      Zaposleni.Cells[0, Row] := Kveri.FieldByName('IDPromocije').AsString;
      Zaposleni.Cells[1, Row] := Kveri.FieldByName('Naziv').AsString;
      Zaposleni.Cells[2, Row] := Kveri.FieldByName('Datum').AsString;
      Zaposleni.Cells[3, Row] := Kveri.FieldByName('Opis').AsString;

      Kveri.Next;
      Inc(Row);
    end;
  finally
    Kveri.Free;
  end;
end;

procedure TfrmListaPromocija.Klijenti_butChange(Sender: TObject);
begin
Klijenti_Label.Visible := True;
Klijenti_cb.Visible := True;
Klijenti.Visible := True;
Zaposleni_Label.Visible := False;
Zaposleni_cb.Visible := False;
Zaposleni.Visible := False;
end;

procedure TfrmListaPromocija.Klijenti_cbChange(Sender: TObject);
var
  MojKveri: TFDQuery;
  Row: Integer;
begin
  MojKveri := TFDQuery.Create(nil);
  try
    MojKveri.Connection := GlobalConnection;
    MojKveri.SQL.Text := 'SELECT Klijenti_Promocije.IDKlijenta, Promocije.IDPromocije, Vrste_Promocija.Naziv, Promocije.Datum, Promocije.Opis ' +
  'FROM Promocije ' +
  'JOIN Vrste_Promocija ON Promocije.Vrsta_Promocije = Vrste_Promocija.IDPromocije ' +
  'JOIN Klijenti_Promocije ON Promocije.IDPromocije = Klijenti_Promocije.IDPromocije '+
  'WHERE Klijenti_Promocije.IDKlijenta = :KlijentID '+
  'ORDER BY julianday(substr(Promocije.Datum, 7, 4) || "-" || substr(Promocije.Datum, 4, 2) || "-" || substr(Promocije.Datum, 1, 2)) DESC';
    MojKveri.ParamByName('KlijentID').AsInteger := GetIDKlijenta(Klijenti_cb.Selected.Text);
    MojKveri.Open;

    Klijenti.RowCount := MojKveri.RecordCount;

    Row := 0;
    while not MojKveri.Eof do
    begin
      Klijenti.Cells[0, Row] := MojKveri.FieldByName('IDPromocije').AsString;
      Klijenti.Cells[1, Row] := MojKveri.FieldByName('Naziv').AsString;
      Klijenti.Cells[2, Row] := MojKveri.FieldByName('Datum').AsString;
      Klijenti.Cells[3, Row] := MojKveri.FieldByName('Opis').AsString;

      MojKveri.Next;
      Inc(Row);
    end;
  finally
    MojKveri.Free;
  end;
end;

procedure TfrmListaPromocija.NazadClick(Sender: TObject);
begin
frmPromocije.show;
self.hide;
end;

function TfrmListaPromocija.GetIDZaposlenog(const imeZaposlenog: string): Integer;
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

function TfrmListaPromocija.GetIDKlijenta(const imeKlijenta: string): Integer;
var
  MyQuery: TFDQuery;
  Ime, Prezime: string;
begin
  Result := -1;
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT idvlasnika, Imevlasnika, Prezimevlasnika FROM vlasnici';
    MyQuery.Open;

    while not MyQuery.Eof do
    begin
      Ime := MyQuery.FieldByName('Imevlasnika').AsString;
      Prezime := MyQuery.FieldByName('Prezimevlasnika').AsString;

      if SameText(imeKlijenta, Ime + ' ' + Prezime) then
      begin
        Result := MyQuery.FieldByName('idvlasnika').AsInteger;
      end;

      MyQuery.Next;
    end;
  finally
    MyQuery.Free;
  end;
end;

end.
