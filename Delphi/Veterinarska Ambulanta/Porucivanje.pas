unit Porucivanje;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs,
  FMX.Controls.Presentation, FMX.StdCtrls, FMX.Edit, FMX.ListBox, System.Rtti,
  FMX.Grid.Style, FMX.ScrollBox, FMX.Grid, FireDAC.Stan.Intf,
  FireDAC.Stan.Option, FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS,
  FireDAC.Phys.Intf, FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt,
  Data.DB, FireDAC.Comp.DataSet, FireDAC.Comp.Client, FMX.Calendar, FMX.Objects, System.Generics.Collections;

type
  TPotrebniLek = class(TObject)
  public
    Ime: string;
    Kolicina: Integer;
    PotrebnaKolicina: Integer;
    CenaPoKomadu: Integer;
  end;

type
  TfrmPoruci = class(TForm)
    Nazad: TButton;
    Dobavljaci: TComboBox;
    Kolicina: TEdit;
    Kolicina_Label: TLabel;
    Naziv_Label: TLabel;
    Datum_Label: TLabel;
    Dodaj: TButton;
    Porudzbenica: TStringGrid;
    ID: TStringColumn;
    NazivNabavke: TStringColumn;
    Kalendar: TCalendar;
    Proizvod: TComboBox;
    Dobavljaci_Label: TLabel;
    Zavrsi: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    KolicinaNabavke: TStringColumn;
    Potrebni: TButton;
    Cena_komad: TIntegerColumn;
    UkupnaCena: TIntegerColumn;
    procedure FormCreate(Sender: TObject);
    procedure NazadClick(Sender: TObject);
    procedure DodajClick(Sender: TObject);
    function GetIDProizvoda(const imeProizvoda: string): Integer;
    function GetCenaProizvoda(const imeProizvoda: string): Integer;
    procedure ZavrsiClick(Sender: TObject);
    function GetIDDobavljaca(const imeDobavljaca: string): Integer;
    procedure PotrebniClick(Sender: TObject);
  private
    { Private declarations }
    SelectedDate: TDate;
    IDProizvoda: Integer;
  public
    { Public declarations }
  end;

var
  frmPoruci: TfrmPoruci;
  Row: Integer = 0;

implementation

{$R *.fmx}

uses
  Main, Nabavka, PotrebniLekovi;

procedure TfrmPoruci.FormCreate(Sender: TObject);
var
  Query: TFDQuery;
  MyQuery: TFDQuery;
begin
  Query := TFDQuery.Create(nil);
  try
    Query.Connection := GlobalConnection;
    Query.SQL.Text := 'SELECT * FROM Dobavljaci';
    Query.Open;

    Dobavljaci.Clear;

    while not Query.Eof do
    begin
      Dobavljaci.Items.Add(Query.FieldByName('Ime').AsString + ' ' + Query.FieldByName('Prezime').AsString);
      Query.Next;
    end;
  finally
    Query.Free;
  end;

  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT * FROM Inventar WHERE Tip = ''LEKOVI''';
    MyQuery.Open;

    Proizvod.Clear;

    while not MyQuery.Eof do
    begin
      Proizvod.Items.Add(MyQuery.FieldByName('Ime').AsString);
      MyQuery.Next;
    end;
  finally
    MyQuery.Free;
  end;
end;

procedure TfrmPoruci.NazadClick(Sender: TObject);
begin
  frmNabavka.Show;
  Self.Hide;
end;

procedure TfrmPoruci.PotrebniClick(Sender: TObject);
var

Kveri: TFDQuery;
begin

  Kveri := TFDQuery.Create(nil);
  try
    Kveri.Connection := GlobalConnection;
    Kveri.SQL.Text := 'SELECT * FROM Inventar WHERE Kolicina < Potrebna_Kolicina';
    Kveri.Open;
    Porudzbenica.RowCount := Kveri.RecordCount;
    Row := 0;

    while not Kveri.Eof do
    begin
      Porudzbenica.Cells[0, Row] := Kveri.FieldByName('ID').AsString;
      Porudzbenica.Cells[1, Row] := Kveri.FieldByName('Ime').AsString;
      Porudzbenica.Cells[2, Row] := inttostr(Kveri.FieldByName('Potrebna_Kolicina').AsInteger - Kveri.FieldByName('Kolicina').AsInteger);
      Porudzbenica.Cells[3, Row] := Kveri.FieldByName('Cena_Po_Komadu').AsString;
      Porudzbenica.Cells[4, Row] := inttostr((Kveri.FieldByName('Potrebna_Kolicina').AsInteger - Kveri.FieldByName('Kolicina').AsInteger)* Kveri.FieldByName('Cena_Po_Komadu').AsInteger);
      Inc(Row);
      Kveri.Next;
    end;
  finally
    Kveri.free;
  end;
end;

procedure TfrmPoruci.ZavrsiClick(Sender: TObject);
var
  MyQuery: TFDQuery;
  i: Integer;
  KolicinaBroj: Integer;
  IDDobavljaca: Integer;
  DatumNabavke: string;
begin
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'INSERT INTO Nabavka (IDProizvoda, IDDobavljaca, Datum_Nabavke, Kolicina, Iznos, Valuta, Status) ' +
      'VALUES (:idproizvoda, :iddobavljaca, :datum, :kolicina, :iznos, :valuta, :status)';

    if Porudzbenica.RowCount > 0 then
    begin
      IDDobavljaca := GetIDDobavljaca(Dobavljaci.Selected.Text);
      SelectedDate := Kalendar.Date;

      for i := 0 to Porudzbenica.RowCount - 1 do
      begin
        IDProizvoda := StrToIntDef(Porudzbenica.Cells[0, i], 0);

        if IDProizvoda <> 0 then
        begin
          KolicinaBroj := StrToIntDef(Porudzbenica.Cells[2, i], 0);
          DatumNabavke := FormatDateTime('dd/mm/yyyy', SelectedDate);

          MyQuery.ParamByName('idproizvoda').AsInteger := IDProizvoda;
          MyQuery.ParamByName('iddobavljaca').AsInteger := IDDobavljaca;
          MyQuery.ParamByName('datum').AsString := DatumNabavke;
          MyQuery.ParamByName('kolicina').AsInteger := KolicinaBroj;
          MyQuery.ParamByName('iznos').AsInteger := StrToIntDef(Porudzbenica.Cells[4, i], 0);
          MyQuery.ParamByName('valuta').AsString := 'RSD';
          MyQuery.ParamByName('Status').AsString := 'ON HOLD';
          MyQuery.ExecSQL;
        end;
      end;

      ShowMessage('Unete sve narudžbine');
    end
    else
      ShowMessage('Porudžbenica je prazna.');
  finally
    MyQuery.Free;
    frmNabavka.Show;
    Self.Hide;
  end;
end;

procedure TfrmPoruci.DodajClick(Sender: TObject);
var
  imeProizvoda: string;
begin
  Porudzbenica.BeginUpdate;
  imeProizvoda := Proizvod.Selected.Text;
  IDProizvoda := GetIDProizvoda(imeProizvoda);
  Porudzbenica.Cells[0, Row] := IntToStr(IDProizvoda);
  Porudzbenica.Cells[1, Row] := Proizvod.Selected.Text;
  Porudzbenica.Cells[2, Row] := Kolicina.Text;
  Porudzbenica.Cells[3, Row] := inttostr(GetCenaProizvoda(imeproizvoda));
  Porudzbenica.Cells[4, Row] := inttostr(strtoint(Kolicina.Text) * GetCenaProizvoda(imeproizvoda));
  Inc(Row);
  Porudzbenica.EndUpdate;

  Proizvod.ItemIndex := -1;
  Kolicina.Text := '';
end;

function TfrmPoruci.GetIDProizvoda(const imeProizvoda: string): Integer;
var
  MyQuery: TFDQuery;
begin
  Result := -1;
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT id FROM inventar WHERE ime = :imeLeka';
    MyQuery.ParamByName('imeLeka').AsString := imeProizvoda;
    MyQuery.Open;

    if not MyQuery.IsEmpty then
      Result := MyQuery.FieldByName('id').AsInteger;
  finally
    MyQuery.Free;
  end;
end;

function TfrmPoruci.GetIDDobavljaca(const imeDobavljaca: string): Integer;
var
  MyQuery: TFDQuery;
  Ime, Prezime: string;
begin
  Result := -1;
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT idDobavljaca, Ime, Prezime FROM Dobavljaci';
    MyQuery.Open;

    while not MyQuery.Eof do
    begin
      Ime := MyQuery.FieldByName('Ime').AsString;
      Prezime := MyQuery.FieldByName('Prezime').AsString;

      if SameText(imeDobavljaca, Ime + ' ' + Prezime) then
      begin
        Result := MyQuery.FieldByName('idDobavljaca').AsInteger;
        Break;
      end;

      MyQuery.Next;
    end;
  finally
    MyQuery.Free;
  end;
end;

function TfrmPoruci.GetCenaProizvoda(const imeProizvoda: string): Integer;
var
  MyQuery: TFDQuery;
begin
  Result := -1;
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT cena_po_komadu FROM inventar WHERE ime = :imeLeka';
    MyQuery.ParamByName('imeLeka').AsString := imeProizvoda;
    MyQuery.Open;

    if not MyQuery.IsEmpty then
      Result := MyQuery.FieldByName('cena_po_komadu').AsInteger;
  finally
    MyQuery.Free;
  end;
end;
end.

