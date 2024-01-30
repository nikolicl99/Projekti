unit VeterinarVakcine;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, System.Rtti,
  FMX.Grid.Style, FMX.Objects, FMX.StdCtrls, FMX.Edit, FMX.Grid, FMX.ScrollBox,
  FMX.ListBox, FMX.Controls.Presentation, FMX.DateTimeCtrls, FireDAC.Stan.Intf,
  FireDAC.Stan.Option, FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS,
  FireDAC.Phys.Intf, FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt,
  Data.DB, FireDAC.Comp.DataSet, FireDAC.Comp.Client;

type
  TfrmVeterinarVakcine = class(TForm)
    Forma: TPanel;
    Vakcine: TComboBox;
    Zavrsi: TButton;
    Nazad: TButton;
    Vakcina_Label: TLabel;
    Revakcinacija: TLabel;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Datum: TDateEdit;
    procedure FormCreate(Sender: TObject);
    procedure DatumChange(Sender: TObject);
    procedure ZavrsiClick(Sender: TObject);
    function GetIDVakcine(const imeVakcine: string): Integer;
    function GetIDKlijenta(const IDtermina: integer): Integer;
  private
    { Private declarations }
    SelectedDate: string;
  public
    { Public declarations }
  end;

var
  frmVeterinarVakcine: TfrmVeterinarVakcine;

implementation

{$R *.fmx}
uses main, pregled, Termini_Veterinar;

procedure TfrmVeterinarVakcine.DatumChange(Sender: TObject);
begin
SelectedDate:= FormatDateTime('dd/mm/yyyy',Datum.Date);
end;

procedure TfrmVeterinarVakcine.FormCreate(Sender: TObject);
var
MyQuery: TFDQuery;
begin
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT * FROM Vakcine';
    MyQuery.Open;

    while not MyQuery.Eof do
    begin
      Vakcine.Items.AddObject(MyQuery.FieldByName('imeVakcine').AsString, TObject(MyQuery.FieldByName('idVakcine').AsInteger));
      MyQuery.Next;
    end;
  finally
    MyQuery.Free;
  end;
end;

procedure TfrmVeterinarVakcine.ZavrsiClick(Sender: TObject);
var
  MyQuery: TFDQuery;
begin
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'INSERT INTO Klijent_Vakcinacija (Klijent, Vakcina, Revakcinacija, Pregled) VALUES (:idklijenta, :idvakcine, :revakcinacija, :idpregleda)';

      MyQuery.ParamByName('idklijenta').AsInteger := getIDKlijenta(frmTerminiVeterinar.FSelectedID);
      MyQuery.ParamByName('idvakcine').AsInteger := getIDVakcine(vakcine.Selected.text);
      MyQuery.ParamByName('revakcinacija').AsString := SelectedDate;
      MyQuery.ParamByName('idpregleda').AsInteger := frmPregled.PregledID;
      MyQuery.ExecSQL;

  finally
    MyQuery.Free;
    ShowMessage('Uneta vakcina');
    frmterminiveterinar.show;
    self.hide;
  end;
end;

function TfrmVeterinarVakcine.GetIDVakcine(const imeVakcine: string): Integer;
var
  MyQuery: TFDQuery;
begin
  Result := -1;
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT idVakcine FROM Vakcine WHERE imeVakcine = :imeVakcine';
    MyQuery.ParamByName('imeVakcine').AsString := imeVakcine;
    MyQuery.Open;

    if not MyQuery.IsEmpty then
      Result := MyQuery.FieldByName('idVakcine').AsInteger;
  finally
    MyQuery.Free;
  end;
end;

function TfrmVeterinarVakcine.GetIDKlijenta(const IDtermina: integer): Integer;
var
  MyQuery: TFDQuery;
begin
  Result := -1;
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT Klijent FROM Termin WHERE IDTermina = :TerminID';
    MyQuery.ParamByName('TerminID').AsInteger := IDtermina;
    MyQuery.Open;

    if not MyQuery.IsEmpty then
      Result := MyQuery.FieldByName('Klijent').AsInteger;
  finally
    MyQuery.Free;
  end;
end;

end.
