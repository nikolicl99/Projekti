unit DodavanjePromocije;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.Memo.Types,
  FMX.ListBox, FMX.StdCtrls, FMX.ScrollBox, FMX.Memo, FMX.Edit,
  FMX.DateTimeCtrls, FMX.Controls.Presentation, FireDAC.Stan.Intf,
  FireDAC.Stan.Option, FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS,
  FireDAC.Phys.Intf, FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt,
  Data.DB, FireDAC.Comp.DataSet, FireDAC.Comp.Client, FMX.Calendar, FMX.Objects;

type
  Tfrmdodavanjepromocije = class(TForm)
    Vrste_Promocija: TComboBox;
    Nazad: TButton;
    Opis: TEdit;
    Dodaj: TButton;
    Datum: TDateEdit;
    Vrsta_Label: TLabel;
    Datum_Label: TLabel;
    Opis_Label: TLabel;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    procedure FormCreate(Sender: TObject);
    procedure NazadClick(Sender: TObject);
    function GetIDVlasnika(const imeVlasnika: string): Integer;
    function GetIDVrste(const imeVrste: string): Integer;
    procedure DodajClick(Sender: TObject);
    procedure DatumChange(Sender: TObject);
  private
    { Private declarations }
    SelectedDate: string;
    IDVrste: integer;
  public
    { Public declarations }
  end;

var
  frmdodavanjepromocije: Tfrmdodavanjepromocije;

implementation

{$R *.fmx}
uses Main, SpisakPromocija;

procedure Tfrmdodavanjepromocije.DatumChange(Sender: TObject);
begin
SelectedDate:= FormatDateTime('dd/mm/yyyy',Datum.Date);
end;

procedure Tfrmdodavanjepromocije.DodajClick(Sender: TObject);
var
MyQuery: TFDQuery;
begin
IDVrste:= GetIDVrste(Vrste_Promocija.Selected.Text);
MyQuery:= TFDQuery.Create(nil);
try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'INSERT INTO Promocije (Vrsta_Promocije, Datum, Opis) VALUES (:IDVrste, :Datum, :Opis)';
    MyQuery.ParamByName('IDVrste').AsInteger := IDVrste;
    MyQuery.ParamByName('Datum').AsString := SelectedDate;
    MyQuery.ParamByName('Opis').AsString := Opis.Text;
    MyQuery.ExecSQL;

  finally
    MyQuery.Free;
    ShowMessage('Promocija uspesno dodata.');
    frmpromocije.show;
    self.hide;
  end;
end;

procedure Tfrmdodavanjepromocije.FormCreate(Sender: TObject);
var
//  MyQuery: TFDQuery;
  Query: TFDQuery;
begin
//  MyQuery := TFDQuery.Create(nil);
//  try
//    MyQuery.Connection := GlobalConnection;
//    MyQuery.SQL.Clear;
//    MyQuery.SQL.Text := 'SELECT * FROM Vlasnici';
//    MyQuery.Open;
//
//    Vlasnici.Clear;
//
//    while not MyQuery.Eof do
//    begin
//      Vlasnici.Items.Add(MyQuery.FieldbyName('ImeVlasnika').AsString + ' ' + MyQuery.FieldByName('PrezimeVlasnika').AsString);
//      MyQuery.Next;
//    end;
//  finally
//    MyQuery.Free;
//    MyQuery.Close;
//  end;
  Query := TFDQuery.Create(nil);
  try
    Query.Connection := GlobalConnection;
    Query.SQL.Clear;
    Query.SQL.Text := 'SELECT * FROM Vrste_Promocija';
    Query.Open;

    Vrste_Promocija.Clear;

    while not Query.Eof do
    begin
      Vrste_Promocija.Items.Add(Query.FieldbyName('Naziv').AsString);
      Query.Next;
    end;
  finally
    Query.Free;
    Query.Close;
  end;

end;

procedure Tfrmdodavanjepromocije.NazadClick(Sender: TObject);
begin
frmpromocije.show;
self.hide;
end;

function TfrmDodavanjePromocije.GetIDVlasnika(const imeVlasnika: string): Integer;
var
  MyQuery: TFDQuery;
  Ime, Prezime: string;
begin
  Result := -1;
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT idvlasnika, ImeVlasnika, PrezimeVlasnika FROM vlasnici';
    MyQuery.Open;

    while not MyQuery.Eof do
    begin
      Ime := MyQuery.FieldByName('ImeVlasnika').AsString;
      Prezime := MyQuery.FieldByName('PrezimeVlasnika').AsString;

      if SameText(imeVlasnika, Ime + ' ' + Prezime) then
      begin
        Result := MyQuery.FieldByName('idvlasnika').AsInteger;
      end;

      MyQuery.Next;
    end;
  finally
    MyQuery.Free;
  end;
end;

function TfrmDodavanjePromocije.GetIDVrste(const imeVrste: string): Integer;
var
  MyQuery: TFDQuery;
begin
  Result := -1;
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT idpromocije FROM vrste_promocija WHERE naziv = :naziv';
    MyQuery.ParamByName('naziv').AsString := imeVrste;
    MyQuery.Open;

    if not MyQuery.IsEmpty then
      Result := MyQuery.FieldByName('idpromocije').AsInteger;
  finally
    MyQuery.Free;
  end;
end;
end.
